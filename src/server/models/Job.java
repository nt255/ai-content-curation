package server.models;

import java.util.Map;
import java.util.UUID;

import common.enums.JobState;
import common.enums.JobType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Job extends BaseModel {
    
    private JobType jobType;
    
    private JobState jobState;

    private UUID id;
    
    private Map<String, String> parameters;
    
    private String textOnlyResult;

}