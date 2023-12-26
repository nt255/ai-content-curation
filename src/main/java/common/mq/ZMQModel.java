package main.java.common.mq;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import main.java.common.models.JobType;


@Getter
@Builder
public class ZMQModel {

    private final UUID id;
    private final UUID baseJobId;

    private final JobType jobType;
    
    private final String params;  // json array of params

}
