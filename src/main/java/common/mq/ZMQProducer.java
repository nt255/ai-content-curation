package main.java.common.mq;

import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class ZMQProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ZMQProducer.class);

    private final Gson gson;
    private final ZMQ.Socket producer;

    @Inject
    public ZMQProducer(Gson gson, Properties properties, ZContext context) {
        this.gson = gson;

        String address = properties.getProperty("zmq.socket.address");

        producer = context.createSocket(SocketType.PUSH);
        producer.bind(address);
        LOG.info("Created producer with address: {}", address);
    }

    public void send(ZMQModel model) {
        verifyJsonArray(model.getParams());

        String s = gson.toJson(model);
        LOG.info("Sending message: {}", s);
        producer.send(s);
    }
    
    public void close() {
        producer.close();
        LOG.info("closed producer");
    }

    // move to some util class in common?
    private void verifyJsonArray(String json) {
        try {
            new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalStateException("found invalid json array");
        }
    }
}
