package enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum  CommandEnum {
    HELP("$help", "lists available commands"),
    REMINDER("$reminder", "sets a reminder, for details enter $reminder", "time", "period", "message");

    String command;
    String description;
    List<String> params = new ArrayList<>();

    CommandEnum(String command, String description) {
        this.command = command;
        this.description = description;
    }

    CommandEnum(String command, String description, String ...param) {
        this.command = command;
        this.description = description;
        params.addAll(Arrays.asList(param));
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return command + " - " + description;
    }
}
