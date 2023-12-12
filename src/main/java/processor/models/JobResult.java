package main.java.processor.models;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobResult {

    private UUID id;
    
    private boolean isSuccessful;
    
    // output
    private String outputText;
    private String outputImagePath;
    private List<String> errors;

}
