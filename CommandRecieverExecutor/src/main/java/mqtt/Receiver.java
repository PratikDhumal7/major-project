package mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import executor.CommandsExecutor;
import json.CommandsList;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Receiver implements MqttCallback {
    private ReceiverClient receiverClient;
    private String shPath;
    private Properties applicationProperies;
    public static Logger logger = LoggerFactory.getLogger(Receiver.class);

    public Receiver(ReceiverClient receiverClient, Properties applicationProperties){
        this.applicationProperies = applicationProperties;
        this.receiverClient = receiverClient;
        this.shPath = applicationProperties.getProperty("sh.path");
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.info("Connection Lost. Try to reconnect");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        logger.info("Received message from " + topic);
        CommandsList commandsList = extractPayloadCommands(mqttMessage);
        appendCommandsToFile(commandsList);
        CommandsExecutor commandsExecutor = new CommandsExecutor(applicationProperies);
        commandsExecutor.executeCommand();
//        receiverClient.relayResults(result);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        logger.info("Delivery Completed!!");
    }

    public CommandsList extractPayloadCommands(MqttMessage mqttMessage){
        String strMessage = new String(mqttMessage.getPayload());
        CommandsList commandsList = new CommandsList();
        String strCommands = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            commandsList = objectMapper.readValue(strMessage, CommandsList.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return commandsList;
    }

    public void appendCommandsToFile(CommandsList commandsList) throws IOException {
        FileWriter writer = new FileWriter(shPath);
        for(String str: commandsList.getCommands()) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
        File file = new File(shPath);
        file.setExecutable(true);
        file.setReadable(true);
    }

    public ReceiverClient getMessageHub() {
        return receiverClient;
    }

    public void setMessageHub(ReceiverClient receiverClient) {
        this.receiverClient = receiverClient;
    }
}
