package json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CommandsList {
    @JsonProperty("commands")
    private List<String> commands;

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
