import org.quartz.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 12/4/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class FTPDownloadListener implements TriggerListener {

    private String name;
    private Logger logger;


    public FTPDownloadListener(String name) {
        this.name = name;
        logger = LogManager.getLogger(FTPDownloadListener.class.getName());

    }

    public String getName() {
        return name;
    }

    public void triggerFired(Trigger trigger,
                             JobExecutionContext context) {
        // do something with the event

        if(trigger.getJobKey().getName().equalsIgnoreCase("myJob")){
            logger.debug("FTP Downloader started");
        CronTriggerMain.setStatus(false);
        }
    }

    public void triggerComplete(Trigger trigger,
                                JobExecutionContext context,
                                Trigger.CompletedExecutionInstruction triggerInstructionCode) {

        // do something with the event
        if(trigger.getJobKey().getName().equalsIgnoreCase("myJob")){
            logger.debug("FTP Downloader completed");
        CronTriggerMain.setStatus(true);
    }
    }

    public void triggerMisfired(Trigger trigger) {
        if(trigger.getJobKey().getName().equalsIgnoreCase("myJob")){
        logger.debug("FTP Downloader misfired");
        }
    }

    public boolean vetoJobExecution(Trigger trigger,
                                    JobExecutionContext context) {
        if(trigger.getJobKey().getName().equalsIgnoreCase("myJob")){
        logger.debug("FTP Downloader veto execution");
        }
        return false;
        // do something with the event
    }
}
