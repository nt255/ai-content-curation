package common.mq;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ZMQPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(ZMQPublisher.class);

    @Inject private Properties properties;
    @Inject private ZContext context;

    private ZMQ.Socket publisher;
    private String topic;

    public void bindSocket() {
        topic = properties.getProperty("zmq.topic");

        SocketType type = SocketType.PUB;
        String address = properties.getProperty("zmq.address");

        LOG.info("Creating publisher with address: {}, topic: {}", address, topic);
        publisher = context.createSocket(type);
        publisher.bind(address);
    }

    public void closeSocket() {
        if (publisher != null) {
            publisher.close();
        }
    }

    public void send(ZMQModel model) {
        String s = new Gson().toJson(model);
        LOG.info("Sending message: {}", s);
        publisher.send(topic + s);
    }

}
