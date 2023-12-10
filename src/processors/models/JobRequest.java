package processors.models;

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
    private String checkpoint;
    private String workflow;

}
