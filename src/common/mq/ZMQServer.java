package common.mq;

import java.util.Properties;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

import common.mq.models.MessageQueueModel;

public class ZMQServer {

    @Inject private Properties properties;
    @Inject private ZContext context;
    
    private ZMQ.Socket socket;

    public void bindSocket() {
        socket = context.createSocket(SocketType.REQ);
        socket.bind(properties.getProperty("zmq.socket.address"));
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

    public void send(MessageQueueModel model) {
        String s = new Gson().toJson(model);
        System.out.println("Sending payload: " + s);
        socket.send(s);
    }

}
