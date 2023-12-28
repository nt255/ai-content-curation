package main.java.common.mq;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ZMQConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ZMQConsumer.class);

    private final Gson gson;
    private final ZMQ.Socket consumer;
    
    @Inject
    public ZMQConsumer(Gson gson, Properties properties, ZContext context) {
        this.gson = gson;

        String address = properties.getProperty("zmq.socket.address");

        consumer = context.createSocket(SocketType.PULL);
        consumer.connect(address);
        LOG.info("Created consumer with address: {}", address);
    }

    public ZMQModel receive() {
        String s = consumer.recvStr();
        LOG.info("Received message: {}", s);
        ZMQModel model = gson.fromJson(s, ZMQModel.class);
        return model;
    }

}
