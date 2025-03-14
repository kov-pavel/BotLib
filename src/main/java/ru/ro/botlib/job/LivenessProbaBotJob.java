package ru.ro.botlib.job;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.ScheduleBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.command.PingBotCommand;
import ru.ro.botlib.exception.BotException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LivenessProbaBotJob extends CustomBotJob {

    private final PingBotCommand pingBotCommand = new PingBotCommand();
    private final User user = new User();
    private final Chat chat = new Chat();
    private final List<String> args = Collections.emptyList();

    @Autowired
    @Qualifier("adminIds")
    private List<Long> adminIds;

    @Autowired
    @Qualifier("botScheduleBuilder")
    private ScheduleBuilder<?> scheduleBuilder;

    public LivenessProbaBotJob() {
        super();
    }

    @PostConstruct
    private void init() {
        super.init(LivenessProbaBotJob.class, scheduleBuilder);
    }

    @Override
    public void executeInner(JobExecutionContext context) throws Exception {
        adminIds.forEach(adminId -> {
            var operationName = "Пинг adminID = " + adminId;
            try {
                log.info(operationName);
                chat.setId(adminId);
                pingBotCommand.executeOne(user, chat, args);
            } catch (Exception ex) {
                BotException.describeLogAndChief(operationName, ex);
            } finally {
                log.info("Пинг завершен.\n");
            }
        });
    }
}
