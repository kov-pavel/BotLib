package ru.ro.botlib.config;

import org.quartz.spi.JobFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

//    @Bean
//    @Primary
//    public SchedulerFactory schedulerFactory() {
//        return new StdSchedulerFactory();
//    }
//
//    @Bean
//    @Primary
//    public Scheduler botScheduler(SchedulerFactory schedulerFactory, AutowiringJobFactory jobFactory) throws Exception {
//        Scheduler scheduler = schedulerFactory.getScheduler();
//        scheduler.setJobFactory(jobFactory);
//        return scheduler;
//    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        var jobFactory = new AutowiringJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean botSchedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory);
        return factory;
    }
}
