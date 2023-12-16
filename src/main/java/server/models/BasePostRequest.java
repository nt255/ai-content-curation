package main.java.server.models;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class BasePostRequest {

    private UUID generatedId;
    
}
