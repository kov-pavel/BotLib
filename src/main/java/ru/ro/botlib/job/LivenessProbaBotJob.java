package ru.ro.botlib.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.command.PingBotCommand;
import ru.ro.botlib.utils.LogUtils;
import ru.ro.botlib.utils.Utils;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class LivenessProbaBotJob extends CustomBotJob {

    private final PingBotCommand pingBotCommand = new PingBotCommand();

    private final User user = new User();
    private final Chat chat = new Chat();
    private final List<String> args = Collections.emptyList();

    private final List<Long> adminids;

    public LivenessProbaBotJob(List<Long> adminids) {
        super(LivenessProbaBotJob.class);
        this.adminids = adminids;
    }

    @Override
    protected void setupInner() throws SchedulerException {
        var job = JobBuilder.newJob(LivenessProbaBotJob.class)
                .withIdentity(jobIdentityName, jobIdentityGroup)
                .build();

        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobTriggerIdentityName, jobIdentityGroup)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(1)
                        .repeatForever()
                ).build();

        scheduler.scheduleJob(job, trigger);
    }

    @Override
    public void executeInner(JobExecutionContext context) throws Exception {
        LogUtils.logBlockSeparator(true);
        log.info("*LivenessProbaJob* [START]");

        adminids.forEach(adminId -> {
            try {
                log.info("\nПинг adminID = {}...", adminId);
                chat.setId(adminId);
                pingBotCommand.executeOne(user, chat, args);
                log.info("Пинг adminID = {} завершен.", adminId);
            } catch (TelegramApiException ex) {
                Utils.CHIEF_NOTIFIER.notifyChief(ex);
            }
        });

        log.info("\n*LivenessProbaJob* [END]");
        LogUtils.logBlockSeparator(false);
    }
}
