package com.ygh.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.ygh.job.MsgDatabaseJob;

/**
 * Quartz配置类
 * @author ygh
 */
@Configuration
public class QuartzConfig {
    
    @Bean
    public JobDetail jobDetail(){
        JobDetail jobDetail = JobBuilder.newJob(MsgDatabaseJob.class)
            .withIdentity("msgDatabaseJob")
            .withDescription("消息存储任务")
            .storeDurably()
            .build();
        return jobDetail;
    }

    @Bean
    public Trigger trigger(){

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail())
            .withIdentity("msgDatabaseJobTrigger")
            .withDescription("消息存储任务触发器")
            .startNow()
            .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(4, 0))
            .build();

        return trigger;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobDetails(jobDetail());
        schedulerFactoryBean.setTriggers(trigger());
        return schedulerFactoryBean;
    }
}
