package common.mq;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

import common.mq.models.ZMQModel;

public class ZMQServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(ZMQServer.class);

    @Inject private Properties properties;
    @Inject private ZContext context;
    
    private ZMQ.Socket socket;

    public void bindSocket() {
        SocketType socketType = SocketType.REQ;
        String socketAddress = properties.getProperty("zmq.socket.address");
        LOG.info("Creating socket of type: {}, address: {}", socketType, socketAddress);
        socket = context.createSocket(socketType);
        socket.connect(socketAddress);
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

    public void send(ZMQModel model) {
        String s = new Gson().toJson(model);
        LOG.info("Sending payload: {}", s);
        socket.send(s);
    }

}
