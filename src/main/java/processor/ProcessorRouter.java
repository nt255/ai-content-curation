package main.java.processor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import main.java.common.enums.JobType;
import main.java.processor.impl.ImageProcessor;
import main.java.processor.impl.TextProcessor;
import main.java.processor.models.JobRequest;
import main.java.processor.models.JobResponse;

public class ProcessorRouter {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessorRouter.class);

    private final Map<JobType, BiFunction<UUID, Map<String, String>, JobResponse>> processorMap;

    @Inject
    public ProcessorRouter(
            TextProcessor textOnlyProcessor, 
            ImageProcessor imageProcessor) {

        processorMap = Map.of(
                JobType.TEXT, (id, params) -> textOnlyProcessor.doWork(
                        getGenericJobRequest(id, params)),
                JobType.IMAGE, (id, params) -> imageProcessor.doWork(
                        getImageJobRequest(id, params)));
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

    private JobRequest getGenericJobRequest(UUID id, Map<String, String> params) {
        return JobRequest.builder()
                .id(id)
                .prompt(params.get("prompt"))
                .build();
    }

    private JobRequest getImageJobRequest(UUID id, Map<String, String> params) {
        return JobRequest.builder()
                .id(id)
                .prompt(params.get("prompt"))
                .height(Integer.parseInt(params.get("height")))
                .checkpoint(params.get("checkpoint"))
                .workflow(params.get("workflow"))
                .build();
    }

}
