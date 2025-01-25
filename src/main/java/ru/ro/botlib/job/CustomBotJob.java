package ru.ro.botlib.job;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.quartz.*;
import org.springframework.stereotype.Component;
import ru.ro.botlib.exception.BotException;
import ru.ro.botlib.utils.log.LogUtils;
import ru.ro.botlib.utils.TimeUtils;

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
        var operationName = "Инициализация джобы " + className;

        try {
            LogUtils.logBlockSeparator(true, operationName);

            this.jobIdentityName = className;
            this.jobIdentityGroup = className;
            this.jobTriggerIdentityName = className + "Trigger";

            var jobKey = new JobKey(jobIdentityName, jobIdentityGroup);

            log.info("Проверка существования джобы...");
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

            log.info("Проверка существования триггера...");
            if (scheduler.checkExists(triggerKey)) {
                log.info("Триггер существует. Удаляю его...");
                scheduler.unscheduleJob(triggerKey);
                log.info("Триггер удален.");
            } else {
                log.info("Триггер появляется впервые.");
            }

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder)
                    .startAt(TimeUtils.nowPlusSeconds(15))
                    .build();

            log.info("Триггер успешно зарегистрирован: {}", LogUtils.parseObjectForLog(trigger));

            scheduler.scheduleJob(job, trigger);
            log.info("Джоба успешно запланирована: {}", LogUtils.parseObjectForLog(job));
        } catch (Exception ex) {
            var describeResponse = BotException.describeLog(operationName, ex);
        } finally {
            LogUtils.logBlockSeparator(false, operationName);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        var operationName = jobIdentityName;
        try {
            LogUtils.logBlockSeparator(true, operationName);

            executeInner(jobExecutionContext);
        } catch (Exception ex) {
            BotException.describeLogAndChief(operationName, ex);
        } finally {
            LogUtils.logBlockSeparator(false, operationName);
        }
    }

    protected void executeInner(JobExecutionContext jobExecutionContext) throws Exception {
        throw new NotImplementedException();
    }
}
