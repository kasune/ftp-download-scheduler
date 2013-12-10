import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 11/14/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class FTPCon {
    static Logger logger;


    public FTPCon() {
        logger = LogManager.getLogger(FTPCon.class.getName());
        logger.debug("ftp connection object initialized ");
    }

    public FTPClient connectToServer(String FTPServer, int FTPPort, String FTPUser, String FTPPasswd, String RDir) throws IOException {
        FTPClient ftp = new FTPClient();
        ftp.connect(FTPServer, FTPPort);
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
            ftp.disconnect();
            logger.debug("ftp not connected");
            System.exit(0);
        }
        logger.debug("connecting to server ....");
        ftp.login(FTPUser, FTPPasswd);
        logger.debug(ftp.getReplyString());

        //enter passive mode
        ftp.enterLocalPassiveMode();
        logger.debug("changing working directory");
        ftp.changeWorkingDirectory(RDir);
        logger.debug(ftp.getReplyString());
        //ftp.makeDirectory("somedir");
        //ftp.changeWorkingDirectory("somedir");
        ftp.setFileType(FTPClient.LOCAL_FILE_TYPE);
        //logger.debug(ftp.doCommand("pwd",""));
        //logger.debug(ftp.getReplyString());
        return ftp;
    }

    public FTPFile[] getRemoteFileList(FTPClient ftp) throws IOException,NullPointerException {

        FTPFile n[] = ftp.listFiles();
        logger.debug("getting remote file list");
        logger.debug(ftp.getReplyString());
        for (FTPFile a : n) {
            logger.debug(a.getName() + " - " + a.getSize());
        }
        return n;

    }

    /*java.io.File srcFolder = new java.io.File("test");
    FileInputStream fis = new FileInputStream(srcFolder);
    ftp.storeFile ("test", fis);*/
    private void renameFile(FTPClient ftp, FTPFile file, long remoteFileSize, boolean LocalFileRename, String PreFix, String LDir, boolean OverWriteFile, boolean RemoteFileDelete) throws IOException {
        if (LocalFileRename) {
            logger.debug("local file rename enabled.");
            logger.debug("file will be rename with pre-fix. " + PreFix);
            PreFix = newFileName(PreFix);
            String localFileName = LDir + File.separator + PreFix.concat(file.getName());
            logger.debug("new file name " + localFileName);
            File NewFile = new File(localFileName);
            if (NewFile.exists() && !OverWriteFile) {
                logger.debug("new file exist and over write disable. download will be skipped");

            } else {
                logger.debug("new File already exist-" + NewFile.exists() + ". Over write -" + OverWriteFile);
                actualDownload(ftp, file, localFileName, remoteFileSize, RemoteFileDelete);
            }
        } else {
            logger.debug("Rename file disable");
        }
    }

    private String newFileName(String PreFix) {

        if (PreFix.equals("yyyyMMddHHmm")) {
            logger.debug("new file name will prefix with current date time");
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 0);
            PreFix = dateFormat.format(cal.getTime()).concat("-");

        }
        return PreFix;
    }

    public void downloadFiles(FTPFile file, FTPClient ftp, boolean LocalFileRename, String LDir, String CurrentDay, String Prefix, boolean OverWriteFile, boolean RemoteFileDelete) throws FileNotFoundException, IOException {


        long remoteFileSize = file.getSize();
        logger.debug("remote file -" + file.getName() + " size -" + remoteFileSize);

        //get the file from the remote system
        if (file.getName().contains(CurrentDay)) {


            //String localFileName = LDir.concat(file.getName());

            String localFileName = LDir + File.separator + file.getName();
            File tempFile = new File(localFileName);


            if (tempFile.exists()) {
                logger.debug(tempFile + " - local file exist in same name. downloading paused......");
                renameFile(ftp, file, remoteFileSize, LocalFileRename, Prefix, LDir, OverWriteFile, RemoteFileDelete);
            } else {

                logger.debug("local file not exist. Downloading file");
                actualDownload(ftp, file, localFileName, remoteFileSize, RemoteFileDelete);

            }

        } else {
            logger.debug("file name not matched. skip.... ");
        }


        //close output stream
    }

    private void actualDownload(FTPClient ftp, FTPFile file, String localFileName, long remoteFileSize, boolean RemoteFileDelete) throws IOException {
        long localFileSize = -1;
        OutputStream output = new FileOutputStream(localFileName);
        logger.debug("download starting......");
        ftp.retrieveFile(file.getName(), output);
        output.close();

        File localFile = new File(localFileName);

        if (localFile.exists()) {
            localFileSize = localFile.length();
        }

        logger.debug("local " + localFileName + " file size -" + localFileSize);
        if (RemoteFileDelete) {
            if (localFileSize == remoteFileSize) {
                logger.debug("deleting remote file as the size matches");
                ftp.deleteFile(file.getName());
            } else {
                logger.debug("didn't delete the remote file" + file.getName() + " as sizes are different");
            }
        } else {
            logger.debug("remote delete disable");
        }

    }

    public void ftpDisconnect(FTPClient ftp) throws IOException, NullPointerException {
        ftp.disconnect();
        logger.debug("ftp disconnected");
    }

    public void emptyLocalDir(File folder, boolean LocalFileDelete) throws IOException {
        if (LocalFileDelete) {
            logger.debug("cleaning local directory before coping....");
            File[] files = folder.listFiles();
            if (files != null) { //some JVMs return null for empty dirs
                for (File f : files) {
                    if (f.isDirectory()) {
                        logger.debug(f.getName() + " is a folder ignore");
                    } else {
                        logger.debug("deleting file in local tmp folder - " + f.getName());
                        f.delete();
                    }
                }
            } else {
                logger.debug("local folder is empty ");
            }
            //folder.delete();
        } else {
            logger.debug("local folder empty disable");
        }
    }
}
