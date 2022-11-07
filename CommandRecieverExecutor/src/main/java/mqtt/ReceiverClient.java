package mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ReceiverClient {
    private IMqttClient relayStation;
    private Receiver receiver;
    private String mqttServer;
    private String channel;
    public static Logger logger = LoggerFactory.getLogger(ReceiverClient.class);

    public ReceiverClient(Properties applicationProperties) throws MqttException {
        this.mqttServer = applicationProperties.getProperty("mqtt.server");
        this.channel = applicationProperties.getProperty("mqtt.channel");
        this.relayStation = new MqttClient(mqttServer, MqttAsyncClient.generateClientId());

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(3000);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setMaxInflight(10);

        this.receiver = new Receiver(this, applicationProperties);
        this.relayStation.connect(mqttConnectOptions);
        this.relayStation.subscribe(this.channel);
        this.relayStation.setCallback(receiver);
    }


    public IMqttClient getRelayStation() {
        return relayStation;
    }

    public void setRelayStation(IMqttClient relayStation) {
        this.relayStation = relayStation;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public String getMqttServer() {
        return mqttServer;
    }

    public void setMqttServer(String mqttServer) {
        this.mqttServer = mqttServer;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
