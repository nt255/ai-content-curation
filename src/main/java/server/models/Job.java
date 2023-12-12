package main.java.server.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import main.java.common.enums.JobState;
import main.java.common.enums.JobType;


@Getter
@Setter
@Builder
public class Job extends BaseModel {
    
    private UUID id;
    
    private Instant createdOn;
    private Instant lastModifiedOn;
    
    private JobType type;
    private JobState state;
    
    private Map<String, String> parameters;
    
    // output
    private String outputText;
    private String outputImagePath;
    private List<String> errors;
    
}
