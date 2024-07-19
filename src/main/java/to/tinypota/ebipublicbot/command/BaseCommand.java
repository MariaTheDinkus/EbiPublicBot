package to.tinypota.ebipublicbot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;
import to.tinypota.ebipublicbot.Main;
import to.tinypota.ebipublicbot.api.BiConsumer;
import to.tinypota.ebipublicbot.bot.Bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseCommand {
    private String name;
    private String description;
    private List<OptionData> options;
    private BiConsumer<Bot, SlashCommandInteractionEvent> commandHandler;

    public BaseCommand(String name, String description) {
        this(name, description, new OptionData[]{});
    }

    public BaseCommand(String name, String description, @Nullable OptionData... options) {
        this.name = name;
        this.description = description;
        if (options != null) {
            this.options = Arrays.stream(options).toList();
        } else {
            this.options = new ArrayList<>();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BiConsumer<Bot, SlashCommandInteractionEvent> getCommandHandler() {
        return commandHandler;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void setCommandHandler(BiConsumer<Bot, SlashCommandInteractionEvent> commandHandler) {
        this.commandHandler = commandHandler;
    }

    public BaseCommand run(BiConsumer<Bot, SlashCommandInteractionEvent> commandHandler) {
        this.commandHandler = commandHandler;
        return this;
    }

    public BaseCommand build(Bot bot) {
        Main.addCommand(this);
        return this;
    }
}
