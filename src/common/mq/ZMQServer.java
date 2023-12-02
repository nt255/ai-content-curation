package common.mq;

import java.util.Properties;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.inject.Inject;

import common.mq.models.MessageQueueModel;
import common.mq.models.MessageQueueModel.JobType;

public class ZMQServer {

    @Inject private Properties properties;
    
    private ZMQ.Socket socket;

    public void bindSocket() {
        try (ZContext context = new ZContext()) {
            socket = context.createSocket(SocketType.REP);
            socket.bind(properties.getProperty("zmq.socket.bind.address"));
        }
    }
    
    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }
    
    public void send(MessageQueueModel model) {
        socket.send(model.toString());
    }

    public MessageQueueModel receive() {
        return MessageQueueModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .build();
    }

}
