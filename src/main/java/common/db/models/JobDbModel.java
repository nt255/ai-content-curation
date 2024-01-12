package main.java.common.db.models;

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
     
     JobState state;
     
}
