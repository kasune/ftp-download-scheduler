import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 11/26/13
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Configurator {
    private static String FTPServer;
    private static int FTPPort;
    private static String FTPUser;
    private static String FTPPasswd;
    private static int RetryCount;
    private static int RetrySleep;
    private static String RDir;
    private static String LocalDir;
    private static boolean FileNameMatch;
    private static String CurrentDay;
    private static int NoOfDaysBack;
    private static boolean RemoteFileDelete;
    private static boolean LocalFileDelete;
    private static boolean LocalFileRename;
    private static String PreFix;
    private static boolean OverWriteFile;
    private static Logger logger;
    private static int NoOfThreads;
    private static long ThreadSleep;
    private static String CronExpression;
    private static String StopCronExpression;
    private static boolean stopFTPCon;
    private static int FTPControlKeepAlive;


    public Configurator() {
        logger = LogManager.getLogger(Configurator.class.getName());

    }

    public void setPara() {

        logger.debug("main configurations loading ");
        Properties prop = new Properties();

        try {
            //load a properties file
            prop.load(new FileInputStream("conf" + File.separator + "main"));

            //get the property value and print it out
            FTPServer = prop.getProperty("FTPServer.host");
            FTPPort = Integer.parseInt(prop.getProperty("FTPServer.port"));
            FTPUser = prop.getProperty("FTPUser");
            FTPPasswd = prop.getProperty("FTPPassword");
            RetryCount = Integer.parseInt(prop.getProperty("RetryCount"));
            RetrySleep = Integer.parseInt(prop.getProperty("RetrySleep"));
            RDir = prop.getProperty("RemoteDirectory");
            LocalDir = prop.getProperty("LocalDirectory");
            FileNameMatch = Boolean.parseBoolean(prop.getProperty("FileNameMatch"));
            NoOfDaysBack = Integer.parseInt(prop.getProperty("DaysBack"));
            RemoteFileDelete = Boolean.parseBoolean(prop.getProperty("RemoteFileDelete"));
            LocalFileDelete = Boolean.parseBoolean(prop.getProperty("LocalFolderEmpty"));
            LocalFileRename = Boolean.parseBoolean(prop.getProperty("LocalFileRename"));
            PreFix = prop.getProperty("LocalFilePreFix");
            OverWriteFile = Boolean.parseBoolean(prop.getProperty("OverwriteFileEnable"));
            NoOfThreads = Integer.parseInt(prop.getProperty("MaxThreadCount"));
            ThreadSleep = Long.parseLong(prop.getProperty("ThreadSleep"));
            FTPControlKeepAlive= Integer.parseInt(prop.getProperty("FTPControlKeepAlive"));

            dateType();

        } catch (IOException ex) {
            logger.error(ex.toString());
        }

    }

    public void setCronExpression() {
        logger.debug("jobs configurations loading ");
        try {
            Properties prop = new Properties();

            prop.load(new FileInputStream("conf" + File.separator + "jobs"));

            CronExpression = prop.getProperty("CronTriggerExpression");
            StopCronExpression = prop.getProperty("StopCronTriggerExpression");
        } catch (IOException ioe) {
            logger.error(ioe.toString());
        }
    }

    public void setAdmin() {
        logger.debug("admin configurations loading ");

        try {
            Properties prop = new Properties();

            prop.load(new FileInputStream("conf" + File.separator + "admin"));

            stopFTPCon = Boolean.parseBoolean(prop.getProperty("StopFTPConnector"));

        } catch (IOException ioe) {
            logger.error(ioe.toString());
        }

    }
     public String getStopCronExpression(){
         return StopCronExpression;
     }
    public boolean getStopAdminCon(){

        return stopFTPCon;
    }

    public int getNoOfThreads() {
        return NoOfThreads;
    }

    public long getThreadSleep() {
        return ThreadSleep;
    }

    public String getFTPServer() {
        return FTPServer;
    }

    public int getFTPPort() {
        return FTPPort;
    }

    public String getFTPUser() {
        return FTPUser;
    }

    public String getFTPPwd() {
        return FTPPasswd;
    }

    public int getRetryCount() {
        return RetryCount;
    }

    public int getRetrySleep() {
        return RetrySleep;
    }

    public String getRemoteDir() {
        return RDir;
    }

    public String getLocalDir() {
        return LocalDir;
    }

    public boolean getFileMatch() {
        return FileNameMatch;
    }

    public String getDayPattern() {
        return CurrentDay;
    }

    public int getNoOfDaysBack() {
        return NoOfDaysBack;
    }

    public boolean getRemoteFileDelete() {
        return RemoteFileDelete;
    }

    public boolean getLocalFileDelete() {
        return LocalFileDelete;
    }

    public boolean getLocalFileRename() {
        return LocalFileRename;
    }

    public String getPrefix() {
        return PreFix;
    }

    public boolean getOverWriteFile() {
        return OverWriteFile;
    }

    public String getCronTime() {
        return CronExpression;
    }
     public int getFTPControlKeepAlive(){
         return FTPControlKeepAlive;
     }

    private void dateType() {
        if (FileNameMatch) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -NoOfDaysBack);
            CurrentDay = dateFormat.format(cal.getTime());
            logger.debug("matching date type -" + CurrentDay);

        } else {
            CurrentDay = "";
            logger.debug("full file list will be downloaded");
        }
    }

}
