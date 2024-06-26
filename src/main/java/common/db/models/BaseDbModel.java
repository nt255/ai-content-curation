package main.java.common.db.models;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class BaseDbModel {
    
    UUID id;
    
    Instant createdOn;
    Instant lastModifiedOn;

}
