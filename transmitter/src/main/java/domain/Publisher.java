package domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Publisher implements Runnable{

    private String destinationAddress;
    private String addressee;
    private final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private IMqttClient mqttClient;
    private Properties applicationProperties;

    public Publisher(Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
        destinationAddress = applicationProperties.getProperty("destination.address");
        addressee = applicationProperties.getProperty("mqtt.topic");
        setupConnection(applicationProperties.getProperty("sender.name"),
                applicationProperties.getProperty("sender.password"));
    }


    private void setupConnection(String senderName, String senderPassword) {
        try {
            mqttClient = new MqttClient(destinationAddress,
                    "publisher_greg" + ThreadLocalRandom.current().nextLong());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setMaxInflight(3);
            options.setKeepAliveInterval(300);
//            options.setUserName(senderName);
//            options.setPassword(senderPassword.toCharArray());

            mqttClient.connect(options);
        } catch (MqttException e) {
            logger.error("Could not instantiate client-> ", e);
        }
    }


    @Override
    public void run() {
        Thread.currentThread().setName("publisher client");

        try {
            byte[] messageBytes = createMsg(applicationProperties).getBytes();
            Thread.sleep(1000);
            logger.info("Start of publisher client thread.");
            mqttClient.publish(addressee, new MqttMessage(messageBytes));
            mqttClient.disconnect();
            mqttClient.close();
            logger.info("Published message -> ", new String(messageBytes));
        } catch (MqttException e) {
            logger.error("Could not send msg.", e);
        }
        catch (InterruptedException e) {
            logger.error("Code was interrupted.", e);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createMsg(Properties applicationProperties) throws JsonProcessingException {
        MsgData msgData = new MsgData();
//        msgData.senderId=applicationProperties.getProperty("sender.id");
//        msgData.password=applicationProperties.getProperty("password");
        String commands=applicationProperties.getProperty("commands");
        msgData.commands= Arrays.asList(commands.split(","));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(msgData);

        return json;
    }
}
