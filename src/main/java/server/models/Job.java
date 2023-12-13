package main.java.server.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.InputType;
import main.java.common.enums.JobState;

@Getter
@Setter
@SuperBuilder
public abstract class Job extends BaseModel {
    
    UUID id;
    
    Instant createdOn;
    Instant lastModifiedOn;
    
    JobState state;
    
    InputType inputType;
    
    String inputText;
    String inputFilename;
    
    List<String> notes;
    List<String> errors;
    
}
