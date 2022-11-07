package executor;

import mqtt.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class CommandsExecutor {
    private Properties applicationProperties;
    private String shPath;
    public static Logger logger = LoggerFactory.getLogger(CommandsExecutor.class);

    public CommandsExecutor(Properties applicationProperties){
        this.applicationProperties = applicationProperties;
        this.shPath = applicationProperties.getProperty("sh.path");
    }

    public void executeCommand() throws IOException {
        try {
            Process process= Runtime.getRuntime().exec(shPath);
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logger.info("Success!");
            } else {
                logger.info("Command could not be executed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
