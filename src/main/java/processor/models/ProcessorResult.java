package main.java.processor.models;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessorResult {

    private final UUID id;
    private final String outputString;

}
