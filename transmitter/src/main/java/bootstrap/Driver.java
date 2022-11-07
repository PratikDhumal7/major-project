package bootstrap;

import domain.Publisher;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Driver {
    public static Properties projectProperties = new Properties();
    public static Logger logger = LoggerFactory.getLogger(Driver.class);

    /**
     --log.file.path
     executionLogs
     --log.level
     info

     --destination.address
     tcp://localhost:1883
     --mqtt.topic
     t2.io/cc/
     --sender.name
        greg
     --sender.password
        greg
     --commands
     ls,echo,cat
     */

    public static void main(String[] args) throws InterruptedException {

//        System.out.println("hey");

        try {
            Thread.currentThread().setName("demo_major_project");
            configureProperties(args);
            configureLogging(projectProperties.getProperty("log.file.path"),
                    projectProperties.getProperty("log.level"));
            Logger logger = LoggerFactory.getLogger(Driver.class);

            logger.info("Starting the demo_major_project thread.");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new Publisher(projectProperties));
            executorService.awaitTermination(1l, TimeUnit.SECONDS);
            executorService.shutdown();
            logger.info("Ending the demo_major_project thread.");
        } catch (Exception e) {
            System.out.println("Could not confiure application" + e.getMessage());
            System.exit(1);
        }


//        System.exit(1);

    }

    public static String configureLogging(String logFile, String logLevel) {
        DailyRollingFileAppender dailyRollingFileAppender = new DailyRollingFileAppender();

        String logFilename = logFile + "/demo.log";
        switch (logLevel) {
            case "DEBUG": {
                dailyRollingFileAppender.setThreshold(Level.toLevel(Priority.DEBUG_INT));
            }
            case "WARN": {
                dailyRollingFileAppender.setThreshold(Level.toLevel(Priority.WARN_INT));
            }
            case "ERROR": {
                dailyRollingFileAppender.setThreshold(Level.toLevel(Priority.ERROR_INT));
            }
            default: {
                dailyRollingFileAppender.setThreshold(Level.toLevel(Priority.INFO_INT));
            }
            break;
        }

        System.out.println("Log files written out at " + logFilename);
        dailyRollingFileAppender.setFile(logFilename);
        dailyRollingFileAppender.setLayout(new EnhancedPatternLayout("%d [%t] %-5p %c - %m%n"));

        dailyRollingFileAppender.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(dailyRollingFileAppender);
        return dailyRollingFileAppender.getFile();
    }

    public static void configureProperties(String[] args) {
        projectProperties = new Properties();
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--")) {
                String key = args[i].replaceAll("--", "");
                projectProperties.put(key, args[i + 1]);
            }
        }
    }

}




