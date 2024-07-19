package to.tinypota.ebipublicbot;

import to.tinypota.ebipublicbot.bot.Bot;
import to.tinypota.ebipublicbot.utils.JsonHelper;
import to.tinypota.ebipublicbot.command.BaseCommand;
import to.tinypota.ebipublicbot.command.MessageCommand;

import java.io.*;
import java.util.*;

public class Main {
    private static String discordToken = System.getenv("PUBLIC_EBI_DISCORD_TOKEN");
    private static ArrayList<MessageCommand> messageCommands = new ArrayList<>();
    private static ArrayList<BaseCommand> commands = new ArrayList<>();
    private static Bot discordBot;

    public static void main(String[] args) {
        discordBot = new Bot("Ebi-san", "with Hina's mind", (bot, event) -> {
            // Used for message received events
        }, (bot, event) -> {
            // Used for slash command events
            for (MessageCommand command : messageCommands) {
                if (event.getName().equals(command.getName())) {
                    event.reply(command.getMessage()).queue();
                }
            }
        }, (bot, event) -> {

        });

        try {
            discordBot.start(discordToken);
            EbiCommands.addCommands(discordBot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BaseCommand> getCommands() {
        return commands;
    }

    public static void addCommand(BaseCommand command) {
        commands.add(command);
    }

    public static ArrayList<MessageCommand> getMessageCommands() {
        return messageCommands;
    }

    public static void loadCommands() throws IOException {
        // Check if is valid MessageCommand
        messageCommands = JsonHelper.loadAllFromJson(MessageCommand.class);
    }
}