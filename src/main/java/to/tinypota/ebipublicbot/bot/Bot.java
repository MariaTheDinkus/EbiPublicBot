package to.tinypota.ebipublicbot.bot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import to.tinypota.ebipublicbot.GuildReactionRoles;
import to.tinypota.ebipublicbot.Main;
import to.tinypota.ebipublicbot.ReactionRoleMessage;
import to.tinypota.ebipublicbot.api.BiConsumer;
import to.tinypota.ebipublicbot.utils.JsonHelper;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Bot extends ListenerAdapter {
    @JsonIgnore
    private JDA bot;
    private String name;
    private String activityMessage;
    @JsonIgnore
    private BiConsumer<Bot, MessageReceivedEvent> messageHandler;
    @JsonIgnore
    private BiConsumer<Bot, SlashCommandInteractionEvent> commandHandler;
    @JsonIgnore
    private BiConsumer<Bot, ButtonInteractionEvent> buttonHandler;

    public Bot(String name, String activityMessage, BiConsumer<Bot, MessageReceivedEvent> messageHandler, BiConsumer<Bot, SlashCommandInteractionEvent> commandHandler, BiConsumer<Bot, ButtonInteractionEvent> buttonHandler) {
        this.name = name;
        this.activityMessage = activityMessage;
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.buttonHandler = buttonHandler;
    }

    public void start(String token) throws Exception {
        bot = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .setActivity(Activity.customStatus("\uD83C\uDF64"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }

    public JDA getBot() {
        return bot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivityMessage() {
        return activityMessage;
    }

    public void setActivityMessage(String activityMessage) {
        this.activityMessage = activityMessage;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        try {
            GuildReactionRoles guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
            for (ReactionRoleMessage message : guildReactionRoles.getReactionRoleMessages().values()) {
                // Check if the message is still in the guild and if so, update it using updateReactionRoleMessage(message)
                event.getGuild().getTextChannelById(message.getChannelId()).retrieveMessageById(message.getMessageId()).queue(
                        msg -> updateReactionRoleMessage(message),
                        e -> guildReactionRoles.removeReactionRoleMessage(message.getMessageId())
                );
            }
        } catch (Exception e) {
            // Ignore if no reaction roles are set up for this guild
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            messageHandler.accept(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            commandHandler.accept(this, event);
            Main.getCommands().forEach(baseCommand -> {
                try {
                    if (baseCommand.getName().equals(event.getName())) {
                        baseCommand.getCommandHandler().accept(this, event);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            buttonHandler.accept(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        try {
            GuildReactionRoles guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
            ReactionRoleMessage reactionRoleMessage = guildReactionRoles.getReactionRoleMessage(event.getMessageIdLong());
            if (reactionRoleMessage != null) {
                String emojiString = event.getReaction().getEmoji().getFormatted();
                Long roleId = reactionRoleMessage.getEmojiRoleMap().get(emojiString);
                if (roleId != null) {
                    Role role = event.getGuild().getRoleById(roleId);
                    if (role != null) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore if it's not a reaction role message
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        try {
            GuildReactionRoles guildReactionRoles = JsonHelper.loadFromJson(GuildReactionRoles.class, "reactionroles_" + event.getGuild().getId());
            ReactionRoleMessage reactionRoleMessage = guildReactionRoles.getReactionRoleMessage(event.getMessageIdLong());
            if (reactionRoleMessage != null) {
                String emojiString = event.getReaction().getEmoji().getFormatted();
                Long roleId = reactionRoleMessage.getEmojiRoleMap().get(emojiString);
                if (roleId != null) {
                    Role role = event.getGuild().getRoleById(roleId);
                    if (role != null) {
                        event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
                    }
                }
            }
        } catch (Exception e) {
            // Ignore if it's not a reaction role message
        }
    }

    public void updateReactionRoleMessage(ReactionRoleMessage reactionRoleMessage) {
        getBot().getTextChannelById(reactionRoleMessage.getChannelId())
                .retrieveMessageById(reactionRoleMessage.getMessageId())
                .queue(message -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.CYAN); // You can change this color

                    StringBuilder descriptionBuilder = new StringBuilder(reactionRoleMessage.getDescription());
                    descriptionBuilder.append("\n\n"); // Add two newlines after the original description

                    for (Map.Entry<String, Long> entry : reactionRoleMessage.getEmojiRoleMap().entrySet()) {
                        Role role = getBot().getRoleById(entry.getValue());
                        if (role != null) {
                            descriptionBuilder.append(entry.getKey())
                                    .append(" ")
                                    .append(role.getName())
                                    .append("\n");
                        }
                    }
                    embedBuilder.setDescription(descriptionBuilder.toString());

                    MessageEmbed embed = embedBuilder.build();
                    message.editMessageEmbeds(embed).queue();

                    // Add new reactions
                    for (String emojiString : reactionRoleMessage.getEmojiRoleMap().keySet()) {
                        // Log emoji string
                        message.addReaction(Emoji.fromFormatted(emojiString)).queue();
                    }
                });
    }

    // TODO: Potentially remove, not super helpful?
    public void clearChannel(TextChannel channel) {
        channel.getHistory().retrievePast(100).queue(messages -> {
            if (!messages.isEmpty()) {
                List<Message> messagesToDelete = messages.stream()
                        .filter(message -> message.getTimeCreated().plusWeeks(2).isAfter(OffsetDateTime.now()))
                        .collect(Collectors.toList());
                channel.purgeMessages(messagesToDelete);
                clearChannel(channel);
            }
        });
    }
}
