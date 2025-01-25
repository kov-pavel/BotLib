package ru.ro.botlib.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.quartz.*;
import org.springframework.stereotype.Component;
import ru.ro.botlib.utils.LogUtils;
import ru.ro.botlib.utils.SDKUtils;

@Component
@Slf4j
public abstract class CustomBotJob implements Job {

    protected String jobIdentityName;
    protected String jobIdentityGroup;
    protected String jobTriggerIdentityName;

    public CustomBotJob(
            Class<? extends CustomBotJob> clazz,
            ScheduleBuilder<?> scheduleBuilder,
            Scheduler scheduler
    ) {
        var className = clazz.getSimpleName();
        log.info("Инициализация джобы {}, START", className);

        try {
            this.jobIdentityName = className;
            this.jobIdentityGroup = className;
            this.jobTriggerIdentityName = className + "Trigger";

            var jobKey = new JobKey(jobIdentityName, jobIdentityGroup);

            log.info("Проверка существования джобы {}, START", LogUtils.parseObjectForLog(jobKey));
            if (scheduler.checkExists(jobKey)) {
                log.info("Джоба существует. Удаляю ее...");
                scheduler.deleteJob(jobKey);
                log.info("Джоба удалена.");
            } else {
                log.info("Джоба появляется впервые.");
            }

            var job = JobBuilder.newJob(clazz)
                    .withIdentity(jobKey)
                    .build();

            var triggerKey = new TriggerKey(jobTriggerIdentityName, jobIdentityGroup);

            log.info("Проверка существования триггера {}, START", LogUtils.parseObjectForLog(triggerKey));
            if (scheduler.checkExists(triggerKey)) {
                log.info("Триггер существует. Удаляю его...");
                scheduler.unscheduleJob(triggerKey);
                log.info("Триггер удален.");
            } else {
                log.info("Триггер появляется впервые.");
            }

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            log.info("Ошибка при инициализации джобы!", ex);
            SDKUtils.CHIEF_NOTIFIER.notifyChief(ex);
        } finally {
            log.info("Инициализация джобы {}, END", className);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            executeInner(jobExecutionContext);
        } catch (Exception ex) {
            SDKUtils.CHIEF_NOTIFIER.notifyChief(ex);
        }
    }

    protected void executeInner(JobExecutionContext jobExecutionContext) throws Exception {
        throw new NotImplementedException();
    }
}
