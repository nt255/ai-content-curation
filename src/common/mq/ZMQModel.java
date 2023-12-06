package common.mq;

import java.util.Map;
import java.util.UUID;

import common.enums.JobType;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ZMQModel {

    private UUID id;

    private JobType jobType;

    private Map<String, String> parameters;

}
