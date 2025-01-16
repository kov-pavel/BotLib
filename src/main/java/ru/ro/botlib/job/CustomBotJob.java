package ru.ro.botlib.job;

import org.apache.commons.lang3.NotImplementedException;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ro.botlib.utils.SDKUtils;

@Component
public abstract class CustomBotJob implements Job {

    protected String jobIdentityName;
    protected String jobIdentityGroup;
    protected String jobTriggerIdentityName;

    public CustomBotJob(
            Class<? extends CustomBotJob> clazz,
            ScheduleBuilder<?> scheduleBuilder,
            Scheduler scheduler
    ) {
        try {
            var className = clazz.getSimpleName();
            this.jobIdentityName = className;
            this.jobIdentityGroup = className;
            this.jobTriggerIdentityName = className + "Trigger";

            var jobKey = new JobKey(jobIdentityName, jobIdentityGroup);

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            var job = JobBuilder.newJob(clazz)
                    .withIdentity(jobKey)
                    .build();

            var triggerKey = new TriggerKey(jobTriggerIdentityName, jobIdentityGroup);

            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey);
            }

            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException ex) {
            SDKUtils.CHIEF_NOTIFIER.notifyChief(ex);
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
