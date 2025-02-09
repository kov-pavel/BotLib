package ru.ro.botlib.job;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import ru.ro.botlib.exception.BotException;
import ru.ro.botlib.utils.TimeUtils;
import ru.ro.botlib.utils.log.LogUtils;

import java.util.Date;

@Slf4j
@NoArgsConstructor
@Component
public abstract class CustomBotJob implements Job {

    @Autowired
    @Qualifier("botSchedulerFactoryBean")
    private SchedulerFactoryBean botSchedulerFactoryBean;

    private Scheduler scheduler;

    @PostConstruct
    private void postConstruct() {
        scheduler = botSchedulerFactoryBean.getScheduler();
    }

    protected void init(
            Class<? extends CustomBotJob> clazz,
            ScheduleBuilder<?> scheduleBuilder
    ) {
        init(clazz, scheduleBuilder, TimeUtils.nowPlusSeconds(3));
    }

    protected void init(
            Class<? extends CustomBotJob> clazz,
            ScheduleBuilder<?> scheduleBuilder,
            Date startTime
    ) {
        var className = clazz.getSimpleName();
        var operationName = "Инициализация джобы " + className;

        try {
            LogUtils.logBlockSeparator(true, operationName);

            var jobIdentityName = className;
            var jobIdentityGroup = className;
            var jobTriggerIdentityName = className + "Trigger";

            var jobKey = new JobKey(jobIdentityName, jobIdentityGroup);

            log.info("Проверка существования джобы...");
            if (this.scheduler.checkExists(jobKey)) {
                log.info("Джоба существует. Удаляю ее...");
                this.scheduler.deleteJob(jobKey);
                log.info("Джоба удалена.");
            } else {
                log.info("Джоба появляется впервые.");
            }

            var job = JobBuilder.newJob(clazz)
                    .withIdentity(jobKey)
                    .build();

            var triggerKey = new TriggerKey(jobTriggerIdentityName, jobIdentityGroup);

            log.info("Проверка существования триггера...");
            if (this.scheduler.checkExists(triggerKey)) {
                log.info("Триггер существует. Удаляю его...");
                this.scheduler.unscheduleJob(triggerKey);
                log.info("Триггер удален.");
            } else {
                log.info("Триггер появляется впервые.");
            }

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder)
                    .startAt(startTime)
                    .build();

            log.info("Триггер успешно зарегистрирован.");

            this.scheduler.scheduleJob(job, trigger);
            log.info("Джоба успешно запланирована.");
        } catch (Exception ex) {
            var describeResponse = BotException.describeLog(operationName, ex);
        } finally {
            LogUtils.logBlockSeparator(false, operationName);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        var operationName = jobExecutionContext.getJobDetail().getKey().getName();
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
