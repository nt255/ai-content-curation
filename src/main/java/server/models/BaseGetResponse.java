package main.java.server.models;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class BaseGetResponse {
    
    final UUID id;
    
    final Instant createdOn;
    final Instant lastModifiedOn;

}
