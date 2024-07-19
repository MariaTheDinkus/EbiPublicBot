package to.tinypota.ebipublicbot;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import to.tinypota.ebipublicbot.bot.Bot;
import to.tinypota.ebipublicbot.command.BaseCommand;

import java.io.IOException;
import java.util.Objects;

public class EbiMiscCommands {
    public static void addCommands(Bot discordBot) {
        var toFahrenheitCommand = new BaseCommand("tofahrenheit", "Convert degrees of Celsius to degrees of Fahrenheit.",
                new OptionData(OptionType.NUMBER, "temp", "The temperature in degrees of Celsius.").setRequired(true)).run((bot, event) -> {
            var tempOption = Objects.requireNonNull(event.getOption("temp"));
            double temp = tempOption.getAsDouble();
            event.reply(String.format("%.1f°C", temp) + " is " + celsiusToFahrenheit(temp)).queue();
        }).build(discordBot);

        var toCelsiusCommand = new BaseCommand("tocelsius", "Convert degrees of Fahrenheit to degrees of Celsius.",
                new OptionData(OptionType.NUMBER, "temp", "The temperature in degrees of Fahrenheit.").setRequired(true)).run((bot, event) -> {
            var tempOption = Objects.requireNonNull(event.getOption("temp"));
            double temp = tempOption.getAsDouble();
            event.reply(String.format("%.1f°F", temp) + " is " + fahrenheitToCelsius(temp)).queue();
        }).build(discordBot);
    }

    // Convert Celsius to Fahrenheit
    public static String celsiusToFahrenheit(double celsius) {
        double fahrenheit = (celsius * 9/5) + 32;
        return String.format("%.1f°F", fahrenheit);
    }

    // Convert Fahrenheit to Celsius
    public static String fahrenheitToCelsius(double fahrenheit) {
        double celsius = (fahrenheit - 32) * 5/9;
        return String.format("%.1f°C", celsius);
    }
}
