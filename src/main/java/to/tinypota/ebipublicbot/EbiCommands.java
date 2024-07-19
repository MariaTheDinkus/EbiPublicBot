package to.tinypota.ebipublicbot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import to.tinypota.ebipublicbot.bot.Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EbiCommands {
    public static void addCommands(Bot discordBot) throws IOException {
        EbiMessageCommands.addCommands(discordBot);
        EbiMiscCommands.addCommands(discordBot);

        updateCommands(discordBot);
    }

    static void updateCommands(Bot discordBot) throws IOException {
        Main.loadCommands();

        ArrayList<CommandData> commandsToAdd = new ArrayList<>();
        Main.getCommands().forEach(baseCommand -> {
            SlashCommandData command = Commands.slash(baseCommand.getName(), baseCommand.getDescription());
            if (!baseCommand.getOptions().isEmpty()) {
                command = command.addOptions(baseCommand.getOptions());
            }
            commandsToAdd.add(command);

            System.out.println("Adding command " + baseCommand.getName() + " to commandsToAdd");
        });

        for (var command : Main.getMessageCommands()) {
            commandsToAdd.add(Commands.slash(command.getName(), command.getDescription()));
            System.out.println("Adding command " + command.getName() + " to commandsToAdd");
        }

        discordBot.getBot().updateCommands().addCommands(commandsToAdd).queue();
    }

    public static boolean isModerator(Member member) {
        return null != findRole(member, 1261900693170688062L) || member.isOwner();
    }

    public static Role findRole(Member member, Long id) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .filter(role -> role.getIdLong() == id)
                .findFirst()
                .orElse(null);
    }

    private static class ReactionRoleData {
        long colorRoleMessageId;
        long pronounRoleMessageId;

        ReactionRoleData(long colorRoleMessageId, long pronounRoleMessageId) {
            this.colorRoleMessageId = colorRoleMessageId;
            this.pronounRoleMessageId = pronounRoleMessageId;
        }
    }
}