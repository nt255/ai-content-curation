package processors.models;

import java.util.Optional;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobRequest {
    
    private UUID id;

    private Optional<String> prompt;

}
