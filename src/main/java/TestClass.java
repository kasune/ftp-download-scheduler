import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * Created with IntelliJ IDEA.
 * User: emkasun
 * Date: 11/18/13
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestClass extends Thread{
    double maxSize;
    File fileName;


    public TestClass(double maxSize,File fileName){
        this.maxSize = maxSize;
         this.fileName = fileName;

    }

    public void run(){
        int count=10;
        boolean checkSize=true;
        while(checkSize){
            double currentSize=fileName.length();
            System.out.println(currentSize);
           if(maxSize/currentSize > 5){
               System.out.print("\r=");
           } if(maxSize/currentSize > 4){
                System.out.print("\r==");
            }
            if(maxSize/currentSize > 3){
                System.out.print("\r===");
            }
            if(maxSize/currentSize > 2){
                System.out.print("\r=====");
            }
            else{
                System.out.print("\r======");
                System.out.print(maxSize/currentSize);
                checkSize=false;
            }
        }
        //System.out.println("test");
         //System.out.println("\r");
    }

    public static void main(String args[]){
        File localFile = new File("D:\\rhel-server-6.4-x86_64-dvd.iso");
         //TestClass t1 = new TestClass(3720347648.0,localFile);
        //t1.start();
        /*System.setProperty("log4j.configurationFile", "C:\\Users\\emkasun\\IdeaProjects\\Rconnect\\conf\\log4j2.xml");
        Logger logger = LogManager.getLogger(TestClass.class.getName());
        System.out.println(System.getProperty("log4j.configurationFile"));
        logger.entry();
        logger.debug("Did it again!");
        logger.exit(false);   */
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -0);
        String CurrentDay = dateFormat.format(cal.getTime());
        System.out.println("Matching date type -" + CurrentDay);
    }
}
