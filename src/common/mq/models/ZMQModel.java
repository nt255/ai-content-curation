package common.mq.models;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ZMQModel {
    
    public enum JobType {
        TEXT_ONLY, IMAGE
    }
    
    private JobType jobType;
    
    private UUID id;
    
    private Map<String, String> parameters;

}
