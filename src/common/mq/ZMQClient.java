package common.mq;

import java.util.Properties;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;
import com.google.inject.Inject;

import common.mq.models.MessageQueueModel;

public class ZMQClient {

    @Inject private Properties properties;
    @Inject private ZContext context;

    private ZMQ.Socket socket;

    public void connectSocket() {
        socket = context.createSocket(SocketType.REP);
        socket.connect(properties.getProperty("zmq.socket.address"));
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

    public MessageQueueModel receive() {
        byte[] reply = socket.recv();
        String s = new String(reply, ZMQ.CHARSET);
        System.out.println("Received payload: " + s);
        MessageQueueModel model = new Gson().fromJson(s, MessageQueueModel.class);
        return model;
    }

}
