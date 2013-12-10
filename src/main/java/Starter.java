import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 11/26/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class Starter extends Thread implements Job {
    static Logger logger;
    static FTPFile list[];
    static int count;
    static int fileCount;
    static String FTPServer;
    static int FTPPort;
    static String FTPUser;
    static String FTPPasswd;
    static int RetryCount;
    static int RetrySleep;
    static String RDir;
    static String LDir;
    static long ThreadSleepTime;
    static boolean FileNameMatch;
    static String CurrentDay;
    static int NoOfDaysBack;
    static boolean RemoteFileDelete;
    static boolean LocalFileDelete;
    static boolean LocalFileRename;
    static String PreFix;
    static boolean OverWriteFile;
    static int NoOfThreads;
    static boolean state;
    static int ControlFTPKeepAlive;
    static int ThreadCounter;

    private static synchronized FTPFile fileRemoveFromList() {
        FTPFile getFile = null;
        if (count < fileCount) {
            getFile = list[count];
            logger.debug("file is going to download " + getFile.getName());
            list[count] = null;
            count++;
        } else {
            logger.debug("no files pending");
        }
        return getFile;
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {


        logger = LogManager.getLogger(Starter.class.getName());
        boolean iniConnectionStatus = false;
        state = true;
        logger.debug("------------- starting ftp connector -------------");
        count = 0;
        Configurator confs = new Configurator();
        confs.setPara();


        Starter st = new Starter();
        st.getParameters();

        try {
            File LocalDirectory = new File(LDir);
            FTPCon objGetFiles = new FTPCon();
            objGetFiles.emptyLocalDir(LocalDirectory, LocalFileDelete);

            FTPClient ftp = st.ftpConnection(0, objGetFiles);
            //test start
            //ftpConnection(0,objGetFiles);
            // FTPCon objCon = new FTPCon();
            //test end
            list = objGetFiles.getRemoteFileList(ftp);
            fileCount = list.length;
            objGetFiles.ftpDisconnect(ftp);
            iniConnectionStatus = true;
        } catch (IOException ioe) {
            logger.error(ioe.toString());
            iniConnectionStatus = false;
        } catch (NullPointerException npe) {
            logger.error(npe.toString());
            //npe.printStackTrace();
            logger.error("exiting main thread as the connection object is null");
            iniConnectionStatus = false;
            //System.exit(0);
        }
        //Object threads[] = new Object[NoOfThreads];
        if (iniConnectionStatus) {
            logger.debug("starting ftp connector downloading.....");

            for (int in = 0; in < NoOfThreads; in++) {

                Starter s1 = new Starter();
                logger.debug("starting download thread - " + s1.getName());
                ThreadCounter++;
                s1.start();

            }

        }
        while (true) {
            if (ThreadCounter == 0) {
                break;
            }
            try {
                //logger.debug("Thread count from while "+ThreadCounter);
                Thread.sleep(5000);
            } catch (InterruptedException ine) {
                logger.error(ine.toString());
            }
        }
        //logger.debug("------------- ftp connector stopped -------------");
    }

    private void getParameters() {
        Configurator conf = new Configurator();
        FTPServer = conf.getFTPServer();
        FTPPort = conf.getFTPPort();
        FTPUser = conf.getFTPUser();
        FTPPasswd = conf.getFTPPwd();
        RetryCount = conf.getRetryCount();
        RetrySleep = conf.getRetrySleep();
        RDir = conf.getRemoteDir();
        FileNameMatch = conf.getFileMatch();
        CurrentDay = conf.getDayPattern();
        NoOfDaysBack = conf.getNoOfDaysBack();
        RemoteFileDelete = conf.getRemoteFileDelete();
        LDir = conf.getLocalDir();
        LocalFileDelete = conf.getLocalFileDelete();
        LocalFileRename = conf.getLocalFileRename();
        PreFix = conf.getPrefix();
        OverWriteFile = conf.getOverWriteFile();
        ThreadSleepTime = conf.getThreadSleep();
        NoOfThreads = conf.getNoOfThreads();
        ControlFTPKeepAlive = conf.getFTPControlKeepAlive();
    }

    private FTPClient ftpConnection(int in, FTPCon objGetFiles) {

        FTPClient ftp = null;

        if (in < RetryCount) {
            try {
                in = in + 1;
                logger.debug("connecting .........");
                ftp = objGetFiles.connectToServer(FTPServer, FTPPort, FTPUser, FTPPasswd, RDir);
                //logger.debug("connected and the ftp object is " + ftp);
                //list = objGetFiles.getRemoteFileList(ftp);
                //fileCount = list.length;
                //objGetFiles.ftpDisconnect(ftp);

            } catch (IOException ioe) {
                //ftp = null;
                //list =null;
                //fileCount =0;

                logger.error(ioe.toString());
                logger.debug("sleeping for " + (RetrySleep / 1000) + " sec");
                try {
                    Thread.sleep(RetrySleep);
                    logger.debug("retrying connection");
                    ftp = ftpConnection(in, objGetFiles);

                } catch (InterruptedException iex) {

                    logger.error(iex.toString());
                }

            }
        } else {
            logger.debug("maximum retry exceed.");
        }/*try{
            FTPFile n[] = ftp.listFiles();
            logger.debug("getting remote file list");
            logger.debug(ftp.getReplyString());
            for (FTPFile a : n) {
                logger.debug(a.getName() + " - " + a.getSize());
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
        }*/

        return ftp;

    }

    public void run() {
        try {

            FTPCon connection = new FTPCon();
            FTPClient ftpClient = ftpConnection(0, connection);
            logger.debug("ftp connection completed");
            for (FTPFile n : list) {

                FTPFile getFile = fileRemoveFromList();
                long startTime = System.currentTimeMillis();
                if (getFile != null) {
                    ftpClient.setControlKeepAliveTimeout(ControlFTPKeepAlive);
                    connection.downloadFiles(getFile, ftpClient, LocalFileRename, LDir, CurrentDay, PreFix, OverWriteFile, RemoteFileDelete);

                    long endTime = System.currentTimeMillis();
                    logger.debug("time taken to download (sec)- " + ((endTime - startTime) / 1000));
                    logger.debug("sleeping for (sec)- " + ThreadSleepTime);
                }
                Thread.sleep(ThreadSleepTime);
            }
            connection.ftpDisconnect(ftpClient);
            ThreadCounter--;
        } catch (IOException ioe) {
            logger.error(ioe.toString());
        } catch (InterruptedException ie) {
            logger.error(ie.toString());
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }
}
