package processors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import common.enums.JobType;
import processors.impl.TextOnlyProcessor;
import processors.models.JobRequest;
import processors.models.JobResponse;

public class ProcessorRouter {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);

    private final Map<JobType, BiFunction<UUID, Map<String, String>, JobResponse>> processorMap;

    @Inject
    public ProcessorRouter(TextOnlyProcessor textOnlyProcessor) {
        processorMap =  Map.of(JobType.TEXT_ONLY, 
                (id, params) -> textOnlyProcessor.doWork(getGenericJobRequest(id, params)));
    }

    public JobResponse route(JobType type, UUID id, Map<String, String> params) {

        String errorMessage = String.format("no matching processor found for JobType: {}", type.name());
        JobResponse errorResponse = JobResponse.builder()
                .id(id)
                .isSuccessful(false)
                .errors(List.of(errorMessage))
                .build();

        return processorMap.getOrDefault(type, (ignoredOne, ignoredTwo) -> {
            LOG.error(errorMessage);
            return errorResponse;
        }).apply(id, params);
    }

    // eventually can have one of these per processor, when more parameters are needed
    private JobRequest getGenericJobRequest(UUID id, Map<String, String> params) {
        return JobRequest.builder()
                .id(id)
                .prompt(Optional.of(params.get("prompt")))
                .build();
    }

}
