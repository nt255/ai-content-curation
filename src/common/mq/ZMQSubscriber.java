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

    private Gson gson;
    private ZMQ.Socket subscriber;
    private String topic;

    
    @Inject
    public void connectSocket(Gson gson, Properties properties, ZContext context) {
        this.gson = gson;
        topic = properties.getProperty("zmq.topic");

        String address = properties.getProperty("zmq.address");

        LOG.info("Creating subscriber with address: {}, topic: {}", address, topic);
        subscriber = context.createSocket(SocketType.SUB);
        subscriber.connect(address);
        subscriber.subscribe(topic);
    }

    public ZMQModel receive() {
        String s = subscriber.recvStr().replaceFirst(topic, "");
        LOG.info("Received message: {}", s);
        ZMQModel model = gson.fromJson(s, ZMQModel.class);
        return model;
    }

}
