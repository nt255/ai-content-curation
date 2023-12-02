package common.mq;

import java.util.Properties;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.inject.Inject;

import common.mq.models.MessageQueueModel;
import common.mq.models.MessageQueueModel.JobType;

public class ZMQClient {
    
    @Inject private Properties properties;
    
    private ZMQ.Socket socket;

    public void connectSocket() {
        try (ZContext context = new ZContext()) {
            socket = context.createSocket(SocketType.REQ);
            socket.connect(properties.getProperty("zmq.socket.connect.address"));
        }
    }
    
    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }


    public MessageQueueModel receive() {
        return MessageQueueModel.builder()
                .jobType(JobType.TEXT_ONLY)
                .build();
    }

}
