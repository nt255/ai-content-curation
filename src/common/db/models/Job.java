package common.db.models;

import java.util.UUID;

import common.enums.JobState;
import common.enums.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Job extends BaseModel {
    
    private JobType jobType;
    
    private JobState jobState;

    private UUID id;
    
    private String prompt;
    
    private String textOnlyResult;

}
