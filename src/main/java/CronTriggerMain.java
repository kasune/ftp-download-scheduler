import org.quartz.*;
import java.io.File;
import java.util.regex.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.apache.logging.log4j.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 12/3/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class CronTriggerMain {
        private static boolean status=true;


    public static void main(String[] args) throws Exception {
        System.setProperty("log4j.configurationFile", "conf" + File.separator + "log4j2.xml");
        System.setProperty("org.terracotta.quartz.skipUpdateCheck", "true");
        Configurator confJobs = new Configurator();
        confJobs.setCronExpression();
        String CronExpressionTime = confJobs.getCronTime();
        String StopCronExpression = confJobs.getStopCronExpression();

        CronTriggerMain cronTrg1 = new CronTriggerMain();
        //System.out.println(CronExpressionTime+"-----------"+StopCronExpression);
        cronTrg1.runNow(CronExpressionTime, StopCronExpression);
        //System.out.println("");

    }

    private void runNow(String CronExpressionTime, String StopCronExpression) throws Exception {
        //CronExpressionTime =  getCronTime();
        //System.out.print(CronExpressionTime);

        SchedulerFactory schedulerFact = new org.quartz.impl.StdSchedulerFactory();

        Scheduler sch = schedulerFact.getScheduler();


        // define the job and tie it to
        JobKey jobKey = new JobKey("myJob", "group1");
        JobDetail job = newJob(Starter.class).withIdentity(jobKey).build();

        TriggerKey triggerKey = new TriggerKey("trigger", "group1");
        Trigger trigger = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(CronExpressionTime)).forJob(job).build();

        // Tell quartz to schedule the job using our trigger

        JobDetail jobStop = newJob(StopAgent.class).withIdentity("stopJob", "group1").build();

        Trigger triggerStop = newTrigger().withIdentity("stopTrigger", "group1").withSchedule(cronSchedule(StopCronExpression)).forJob(jobStop).build();

        sch.getListenerManager().addTriggerListener(new FTPDownloadListener("listenMain"));
        sch.start();
        sch.scheduleJob(jobStop, triggerStop);

        sch.scheduleJob(job, trigger);

        //sch.getListenerManager().addTriggerListener(FTPDownloadListener,  TriggerKey("myJob", "group1"));
        //sch.shutdown(true);

    }

    public static void setStatus(boolean in){
        status=in;
    }
    public static boolean getStatus(){
        return status;
    }
}

