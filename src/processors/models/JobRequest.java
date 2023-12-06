package processors.models;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobRequest {
    
    private UUID id;

    private String prompt;

}
