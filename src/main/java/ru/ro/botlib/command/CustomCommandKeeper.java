package ru.ro.botlib.command;

import java.util.ArrayList;
import java.util.List;

public class CustomCommandKeeper {

    public List<CustomBotCommand> getAllCommands() {
        try {
            var allCommands = new ArrayList<CustomBotCommand>();
            var fields = this.getClass().getDeclaredFields();

            for (var field : fields) {
                if (CustomBotCommand.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value != null) {
                        allCommands.add((CustomBotCommand) value);
                    }
                }
            }

            return allCommands;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
