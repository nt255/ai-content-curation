package main.java.common.db.models;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.JobState;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class JobDbModel extends BaseDbModel {
    
     UUID id;
     
     Instant createdOn;
     Instant lastModifiedOn;
     
     JobState state;
     
}
