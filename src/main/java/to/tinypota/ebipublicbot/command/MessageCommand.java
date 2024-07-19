package to.tinypota.ebipublicbot.command;

public class MessageCommand {
    private String name;
    private String description;
    private String message;

    public MessageCommand() {

    }

    public MessageCommand(String name, String description, String message) {
        this.name = name;
        this.description = description;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
