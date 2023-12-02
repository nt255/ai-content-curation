package common.mq;

import java.util.Properties;

import com.google.inject.Inject;

import common.mq.models.MessageQueueModel;

public class ZeroMQImpl {
    
    @Inject private Properties properties;
    
    public void createSocket() {
        
    }
    
    public MessageQueueModel receive() {
        return null;
    }
    
    public void send(MessageQueueModel model)  {
        
    }

}
