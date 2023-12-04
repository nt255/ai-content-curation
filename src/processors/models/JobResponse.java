package processors.models;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobResponse {

    private UUID id;
    
    private boolean isSuccessful;
    
    private List<String> errors;

}
