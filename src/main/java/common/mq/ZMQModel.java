package main.java.common.mq;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import main.java.common.enums.JobType;


@Builder
@Getter
public class ZMQModel {

    private UUID id;

    private JobType jobType;

    private Map<String, String> parameters;

}
