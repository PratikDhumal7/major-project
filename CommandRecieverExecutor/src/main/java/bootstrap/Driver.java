package bootstrap;

import mqtt.ReceiverClient;
import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Driver {
    public static Properties projectProperties = new Properties();
    public static Logger logger = LoggerFactory.getLogger(Driver.class);

    /**
     * @param args --log.file.path executionLogs --log.level INFO --mqtt.channel "t2.io/" --mqtt.server "tcp://localhost:1883" --sh.path "/home/edwin/Documents/workspace/CommandRecieverExecutor/commands.sh"
     * @throws Exception 0 - log.file.path
     *                   1 - log.level
     *                   2 - mqtt.channel
     *                   3 - mqtt.server
     *                   4 - sh.path
     **/

    public static void main(String[] args) throws Exception {
        loadProjectProperties(args);

        Boolean debugLogEnabled = Boolean.parseBoolean(projectProperties.getProperty("debug.log.enabled"));
        configureLogging(debugLogEnabled, projectProperties.getProperty("log.file.path"));

        ReceiverClient receiverClient = new ReceiverClient(projectProperties);
    }

    public static String configureLogging(boolean debug, String filePath) {
        FileAppender fa = new FileAppender();

        if (!debug) {
            fa.setThreshold(Level.toLevel(Priority.INFO_INT));
            fa.setFile(filePath + "receiverLogs.log");
        } else {
            fa.setThreshold(Level.toLevel(Priority.DEBUG_INT));
            fa.setFile(filePath + "receiverDebug.log");
        }

        fa.setLayout(new EnhancedPatternLayout("%-6d [%25.35t] %-5p %40.80c - %m%n"));

        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        return fa.getFile();
    }

    public static void loadProjectProperties(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--")) {
                projectProperties.put(args[i].replaceAll("--", ""), args[i + 1]);
            }
        }
    }
}
