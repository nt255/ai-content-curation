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
    
    private Gson gson;
    private String topic;
    private ZMQ.Socket publisher;
    
    
    @Inject
    public ZMQPublisher(Gson gson, Properties properties, ZContext context) {
        this.gson = gson;
        topic = properties.getProperty("zmq.topic");

        String address = properties.getProperty("zmq.address");

        LOG.info("Creating publisher with address: {}, topic: {}", address, topic);
        publisher = context.createSocket(SocketType.PUB);
        publisher.bind(address);
    }

    public void send(ZMQModel model) {
        String s = gson.toJson(model);
        LOG.info("Sending message: {}", s);
        publisher.send(topic + s);
    }

}
