package common.mq.models;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class MessageQueueModel {
    
    public enum JobType {
        TEXT_ONLY, IMAGE
    }
    
    private JobType jobType;
    
    private Map<String, String> parameters;

}
