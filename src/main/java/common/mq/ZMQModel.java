package main.java.common.mq;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import main.java.common.models.JobType;


@Getter
@Builder
public class ZMQModel {

    private UUID id;

    private JobType jobType;

    // json string of a BaseParams instance
    // using BaseParams directly leads to loss of params when converting
    private String params;

}
