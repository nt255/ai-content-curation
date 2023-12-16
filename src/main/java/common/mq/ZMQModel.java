package main.java.common.mq;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import main.java.common.models.JobType;


@Getter
@Builder
public class ZMQModel {

    private UUID id;
    private UUID baseJobId;

    private JobType jobType;
    
    // private String steps;   // json string of param types
    private String params;  // json string of params

}
