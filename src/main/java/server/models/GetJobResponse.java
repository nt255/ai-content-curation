package main.java.server.models;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import main.java.common.models.JobState;

@Getter
@Setter
@SuperBuilder
public abstract class GetJobResponse extends BaseGetResponse {
    
    final JobState state;

}
