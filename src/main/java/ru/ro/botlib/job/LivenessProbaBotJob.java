package ru.ro.botlib.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.command.PingBotCommand;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LivenessProbaBotJob extends CustomBotJob {

    private PingBotCommand pingBotCommand;
    private final User user = new User();
    private final Chat chat = new Chat();
    private final List<String> args = Collections.emptyList();

    private final List<Long> adminIds;

    public LivenessProbaBotJob(
            List<Long> adminIds,
            @Qualifier("botScheduleBuilder") ScheduleBuilder<?> scheduleBuilder,
            Scheduler scheduler
    ) {
        super(LivenessProbaBotJob.class, scheduleBuilder, scheduler);
        this.adminIds = adminIds;
    }

    @Autowired
    private void setPingBotCommand(PingBotCommand pingBotCommand) {
        this.pingBotCommand = pingBotCommand;
    }

    @Override
    public void executeInner(JobExecutionContext context) throws Exception {
        adminIds.forEach(adminId -> {
            try {
                log.info("\nПинг adminID = {}", adminId);
                chat.setId(adminId);
                pingBotCommand.executeOne(user, chat, args);
            } catch (TelegramApiException ex) {
                var errorMsg = String.format("Не получается пингануть adminID = %d", adminId);
                throw new RuntimeException(errorMsg, ex);
            } finally {
                log.info("Пинг завершен.");
            }
        });
    }
}
