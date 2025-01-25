package ru.ro.botlib.command;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CustomCommandKeeper {

    public List<CustomBotCommand> getAllCommands() {
        try {
            log.info("Генерация списка всех команд бота, START");

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
        } finally {
            log.info("Генерация списка всех команд бота, END");
        }
    }
}
