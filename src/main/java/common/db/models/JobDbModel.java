package main.java.common.db.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.enums.InputType;
import main.java.common.enums.JobState;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class JobDbModel extends BaseDbModel {
    
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
