package to.tinypota.ebipublicbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import to.tinypota.ebipublicbot.bot.Bot;
import to.tinypota.ebipublicbot.utils.JsonHelper;
import to.tinypota.ebipublicbot.command.BaseCommand;
import to.tinypota.ebipublicbot.command.MessageCommand;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EbiMessageCommands {
    public static void addCommands(Bot discordBot) {
        var listMessageCommand = new BaseCommand("listmsgcommands", "List all message commands currently active").run((bot, event) -> {
            // Use moderator check to cancel command if not a moderator
            if (!EbiCommands.isModerator(event.getMember())) {
                event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Message Commands");
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField("Name", "Description", false);
            for (MessageCommand command : Main.getMessageCommands()) {
                embedBuilder.addField(command.getName(), command.getDescription(), false);
            }
            event.replyEmbeds(embedBuilder.build()).queue();
        }).build(discordBot);

        var refreshMessageCommand = new BaseCommand("refreshmessagecommands", "Refresh the message commands").run((bot, event) -> {
            EbiCommands.updateCommands(bot);
        }).build(discordBot);

        var addMessageCommand = new BaseCommand("addmessagecommand", "Add a message command.",
                new OptionData(OptionType.STRING, "name", "The name of the command", true),
                new OptionData(OptionType.STRING, "description", "The description of the command", true),
                new OptionData(OptionType.STRING, "message", "The message to be sent when the command is used", true)
        ).run((bot, event) -> {
            // Use moderator check to cancel command if not a moderator
            if (!EbiCommands.isModerator(event.getMember())) {
                event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                return;
            }

            // Get the name, description, and message from the event
            var nameOption = Objects.requireNonNull(event.getOption("name"));
            var descriptionOption = Objects.requireNonNull(event.getOption("description"));
            var messageOption = Objects.requireNonNull(event.getOption("message"));
            String name = nameOption.getAsString();
            String description = descriptionOption.getAsString();
            String message = messageOption.getAsString();

            // Save the command as a json file using JsonHelper.saveToJson(T object, String jsonName)
            MessageCommand command = new MessageCommand(name, description, message);
            JsonHelper.saveToJson(command, name);
            EbiCommands.updateCommands(bot);

            // Give detailed feedback using an embed in event.reply
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Added command " + name);
            embedBuilder.setColor(Color.RED);
            embedBuilder.addField("Description", description, false);
            embedBuilder.addField("Message", message, false);
            event.replyEmbeds(embedBuilder.build()).queue();
        }).build(discordBot);

        var removeMessageCommand = new BaseCommand("removemessagecommand", "Remove a message command.",
                new OptionData(OptionType.STRING, "name", "The name of the command to remove", true)
        ).run((bot, event) -> {
            // Use moderator check to cancel command if not a moderator
            if (!EbiCommands.isModerator(event.getMember())) {
                event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                return;
            }

            //Get the name
            var nameOption = Objects.requireNonNull(event.getOption("name"));
            String name = nameOption.getAsString();

            // Delete the json file using JsonHelper.removeJson(String jsonName)
            JsonHelper.removeJson(name);
            EbiCommands.updateCommands(bot);

            // Give feedback using event.reply
            event.reply("Removed command " + name).queue();
        }).build(discordBot);

        var createReactionRoleMessage = new BaseCommand("createrolemessage", "Create a new reaction role message",
                new OptionData(OptionType.CHANNEL, "channel", "The channel to create the message in", true),
                new OptionData(OptionType.STRING, "description", "The description of the reaction role message", true)
        ).run((bot, event) -> {
            var channelOption = Objects.requireNonNull(event.getOption("channel"));
            var descriptionOption = Objects.requireNonNull(event.getOption("description"));
            var channel = channelOption.getAsChannel().asTextChannel();
            var description = descriptionOption.getAsString();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.setDescription(description);

            channel.sendMessageEmbeds(embedBuilder.build()).queue(message -> {
                var reactionRoleMessage = new ReactionRoleMessage(
                        message.getIdLong(),
                        channel.getIdLong(),
                        description,
                        new HashMap<>()
                );
                updateGuildReactionRoles(bot, event.getGuild().getIdLong(), reactionRoleMessage);
                event.reply("Reaction role message created with ID: " + message.getId()).setEphemeral(true).queue();
            });
        }).build(discordBot);

        var addReactionRole = new BaseCommand("addrole", "Add a reaction role to a message",
                new OptionData(OptionType.STRING, "messageid", "The ID of the reaction role message", true),
                new OptionData(OptionType.STRING, "emoji", "The emoji for the reaction role (Unicode or custom)", true),
                new OptionData(OptionType.ROLE, "role", "The role to assign", true)
        ).run((bot, event) -> {
            // Use moderator check to cancel command if not a moderator
            if (!EbiCommands.isModerator(event.getMember())) {
                event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                return;
            }

            var messageIdOption = Objects.requireNonNull(event.getOption("messageid"));
            var emojiOption = Objects.requireNonNull(event.getOption("emoji"));
            var roleOption = Objects.requireNonNull(event.getOption("role"));

            long messageId = Long.parseLong(messageIdOption.getAsString());
            String emojiString = emojiOption.getAsString();
            Role role = roleOption.getAsRole();

            try {
                Emoji emoji = Emoji.fromFormatted(emojiString);
                GuildReactionRoles guildReactionRoles;
                try {
                    guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
                } catch (Exception e) {
                    guildReactionRoles = new GuildReactionRoles(event.getGuild().getIdLong(), new HashMap<>());
                }
                ReactionRoleMessage reactionRoleMessage = guildReactionRoles.getReactionRoleMessage(messageId);
                if (reactionRoleMessage == null) {
                    event.reply("Error: Could not find the reaction role message. Make sure the message ID is correct.").setEphemeral(true).queue();
                    return;
                }
                reactionRoleMessage.addReactionRole(emoji.getFormatted(), role.getIdLong());
                updateGuildReactionRoles(bot, event.getGuild().getIdLong(), reactionRoleMessage);
                bot.updateReactionRoleMessage(reactionRoleMessage);
                event.reply("Reaction role added successfully").setEphemeral(true).queue();
            } catch (Exception e) {
                event.reply("Error: Could not add reaction role. " + e.getMessage()).setEphemeral(true).queue();
            }
        }).build(discordBot);

        var removeReactionRole = new BaseCommand("removerole", "Remove a reaction role from a message",
                new OptionData(OptionType.STRING, "messageid", "The ID of the reaction role message", true),
                new OptionData(OptionType.STRING, "emoji", "The emoji of the reaction role to remove", true)
        ).run((bot, event) -> {
            // Use moderator check to cancel command if not a moderator
            if (!EbiCommands.isModerator(event.getMember())) {
                event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                return;
            }

            var messageIdOption = Objects.requireNonNull(event.getOption("messageid"));
            var emojiOption = Objects.requireNonNull(event.getOption("emoji"));

            long messageId = Long.parseLong(messageIdOption.getAsString());
            String emojiString = emojiOption.getAsString();

            try {
                GuildReactionRoles guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
                ReactionRoleMessage reactionRoleMessage = guildReactionRoles.getReactionRoleMessage(messageId);
                if (reactionRoleMessage == null) {
                    event.reply("Error: Could not find the reaction role message. Make sure the message ID is correct.").queue();
                    return;
                }
                reactionRoleMessage.removeReactionRole(emojiString);
                JsonHelper.saveToJson(guildReactionRoles, "reactionroles_" + event.getGuild().getId());

                // Remove only the specific reaction
                event.getChannel().retrieveMessageById(messageId).queue(message -> {
                    message.removeReaction(Emoji.fromFormatted(emojiString)).queue();
                });

                bot.updateReactionRoleMessage(reactionRoleMessage);
                event.reply("Reaction role removed successfully").queue();
            } catch (Exception e) {
                event.reply("Error: Could not remove reaction role. " + e.getMessage()).queue();
            }
        }).build(discordBot);

        // Refresh all reaction role messages command
        var refreshReactionRole = new BaseCommand("refreshroles", "Refreshes all reaction messages (for if a role is changed)")
                .run((bot, event) -> {
                    // Use moderator check to cancel command if not a moderator
                    if (!EbiCommands.isModerator(event.getMember())) {
                        event.reply("You must be a moderator to use this command").setEphemeral(true).queue();
                        return;
                    }

                    GuildReactionRoles guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
                    // Loop through all reaction role messages and update them
                    for (ReactionRoleMessage reactionRoleMessage : guildReactionRoles.getReactionRoleMessages().values()) {
                        bot.updateReactionRoleMessage(reactionRoleMessage);
                    }
                }).build(discordBot);
    }

    private static void updateGuildReactionRoles(Bot bot, long guildId, ReactionRoleMessage reactionRoleMessage) {
        try {
            GuildReactionRoles guildReactionRoles;
            try {
                guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + guildId);
            } catch (Exception e) {
                // If the file doesn't exist or can't be deserialized, create a new GuildReactionRoles
                guildReactionRoles = new GuildReactionRoles(guildId, new HashMap<>());
            }
            guildReactionRoles.addReactionRoleMessage(reactionRoleMessage);
            JsonHelper.saveToJson(guildReactionRoles, "reactionroles_" + guildId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
