package main.java.processor.models;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobResponse {

    private UUID id;
    
    private boolean isSuccessful;
    
    private String textResult;
    
    private String imageResultFilePath;
    
    private List<String> errors;

}
