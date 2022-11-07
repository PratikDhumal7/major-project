package domain;

import java.util.ArrayList;
import java.util.List;

public class MsgData {
   ;
//    String password;
    List<String> commands= new ArrayList<String>();


    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
