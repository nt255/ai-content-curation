package common.mq;

import java.util.Map;
import java.util.UUID;

import common.enums.JobType;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class ZMQModel {

    private JobType jobType;

    private UUID id;

    private Map<String, String> parameters;

}
