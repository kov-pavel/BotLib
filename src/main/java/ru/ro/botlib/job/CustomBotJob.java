package ru.ro.botlib.job;

import org.apache.commons.lang3.NotImplementedException;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.ro.botlib.utils.Utils;

public abstract class CustomBotJob implements Job {

    public static final Scheduler scheduler;
    private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();

    static {
        try {
            scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    protected String jobIdentityName;
    protected String jobIdentityGroup;
    protected String jobTriggerIdentityName;

    public CustomBotJob(Class<? extends CustomBotJob> clazz) {
        init(clazz);
    }

    protected void init(Class<? extends CustomBotJob> clazz) {
        var className = clazz.getSimpleName();
        this.jobIdentityName = className;
        this.jobIdentityGroup = className;
        this.jobTriggerIdentityName = className + "Trigger";

        setup();
    }

    private void setup() {
        try {
            setupInner();
        } catch (SchedulerException ex) {
            Utils.CHIEF_NOTIFIER.notifyChief(ex);
        }
    }

    protected void setupInner() throws SchedulerException {
        throw new NotImplementedException();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            executeInner(jobExecutionContext);
        } catch (Exception ex) {
            Utils.CHIEF_NOTIFIER.notifyChief(ex);
        }
    }

    protected void executeInner(JobExecutionContext jobExecutionContext) throws Exception {
        throw new NotImplementedException();
    }
}
