package main.java.processor.models;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobRequest {
    
    private UUID id;

    private String prompt;
    
    // image only
    private Integer height;
    private Integer width;
    private String checkpoint;
    private String workflow;

}
