package server.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import common.enums.JobState;
import common.enums.JobType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


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
    
    private String textResult;
    
    private String imageResult;
    
    private List<String> processingNotes;
    
}