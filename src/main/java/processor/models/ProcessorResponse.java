package main.java.processor.models;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessorResponse {

    private UUID id;
    
    private boolean isSuccessful;

    private String outputString;
    
    private List<String> notes;
    private List<String> errors;

}
