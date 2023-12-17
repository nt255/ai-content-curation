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
    
    private String params;  // json array of params

}
