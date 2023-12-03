package common.mq;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ZMQSubscriber {

    private static final Logger LOG = LoggerFactory.getLogger(ZMQSubscriber.class);

    @Inject private Properties properties;
    @Inject private ZContext context;

    private ZMQ.Socket subscriber;
    private String topic;

    public void connectSocket() {
        topic = properties.getProperty("zmq.topic");

        SocketType type = SocketType.SUB;
        String address = properties.getProperty("zmq.address");

        LOG.info("Creating subscriber with address: {}, topic: {}", address, topic);
        subscriber = context.createSocket(type);
        subscriber.connect(address);
        subscriber.subscribe(topic);
    }

    public void closeSocket() {
        if (subscriber != null) {
            subscriber.close();
        }
    }

    public ZMQModel receive() {
        String s = subscriber.recvStr().replaceFirst(topic, "");
        LOG.info("Received message: {}", s);
        ZMQModel model = new Gson().fromJson(s, ZMQModel.class);
        return model;
    }

}
