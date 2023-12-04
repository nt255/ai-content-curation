package common.db.models;

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
    
    private String textOnlyResult;

}
