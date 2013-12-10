import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 12/3/13
 * Time: 4:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class StopAgent implements Job {
    static Logger logger;
     //this will use to stop the connector.
    //it monitors all of the thread and the admin variable state.

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        logger = LogManager.getLogger(StopAgent.class.getName());
        Configurator confs = new Configurator();
        confs.setAdmin();
        boolean stopAgent = confs.getStopAdminCon();
        logger.debug("checking FTP Connector state");
        try {
            com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

            logger.debug("Available Processors = " + operatingSystemMXBean.getAvailableProcessors());
            logger.debug("System Load Avg = " + operatingSystemMXBean.getSystemLoadAverage());
            logger.debug("Process CPU Load = " + operatingSystemMXBean.getProcessCpuLoad());
            logger.debug("Total Physical Memory(bytes) = " + operatingSystemMXBean.getTotalPhysicalMemorySize());
            logger.debug("Free Physical Memory(bytes) = " + operatingSystemMXBean.getFreePhysicalMemorySize());
            logger.debug("System CPU Load = " + operatingSystemMXBean.getSystemCpuLoad());
            logger.debug("Note: -1 means invalid value, as system not supported.");

        //if(context.getScheduler().getTriggerState(new TriggerKey("trigger", "group1"))== context.getScheduler().getTriggerState(new TriggerKey("stopTrigger", "group2"))){

        //}

           // logger.debug(context.getScheduler().getTriggerState(new TriggerKey("trigger", "group1")));
           // logger.debug(context.getScheduler().getTriggerState(new TriggerKey("stopTrigger", "group1")));

            if (stopAgent && CronTriggerMain.getStatus()) {
                logger.debug("FTPConnector configuration ready to stop -"+stopAgent +" FTPConnector threads ready to stop - " + CronTriggerMain.getStatus());
                logger.debug("------------- stopping ftp connector -------------");
                context.getScheduler().shutdown();

            } else {
                logger.debug("FTPConnector configuration ready to stop -" + stopAgent+" FTPConnector threads ready to stop - "+CronTriggerMain.getStatus());
            }
        } catch (SchedulerException scex) {
            logger.error(scex.toString());
        }
    }
}
