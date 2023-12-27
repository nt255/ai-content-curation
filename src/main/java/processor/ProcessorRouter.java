package main.java.processor;

import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import main.java.common.models.BaseParams;
import main.java.common.models.JobType;
import main.java.common.models.image.ImageParams;
import main.java.common.models.text.TextParams;
import main.java.processor.image.ImageProcessor;
import main.java.processor.models.ProcessorResult;
import main.java.processor.text.TextProcessor;

class ProcessorRouter {

    private final Gson gson;
    private final Map<JobType, BiFunction<UUID, String, ProcessorResult>> processorMap;

    @Inject
    public ProcessorRouter(Gson gson,
            TextProcessor textProcessor, ImageProcessor imageProcessor) {

        this.gson = gson;

        // unfortunately Java can't cast to inferred T from processor at runtime
        // otherwise would have TypeToken<List<T>> in processAndSave
        Type listOfTextParams = new TypeToken<List<TextParams>>() {}.getType();
        Type listOfImageParams = new TypeToken<List<ImageParams>>() {}.getType();

        processorMap = Map.of(
                JobType.TEXT, (id, params) -> processAndSave(
                        listOfTextParams, textProcessor, id, params),
                JobType.IMAGE, (id, params) -> processAndSave(
                        listOfImageParams, imageProcessor, id, params));
    }

    ProcessorResult processAndSave(JobType type, UUID id, String params) {
        return processorMap.getOrDefault(type, (ignoredOne, ignoredTwo) -> {
            String errorMessage = String.format(
                    "no processor found for JobType: {}", type.name());
            throw new IllegalStateException(errorMessage);
        }).apply(id, params);
    }


    private <T extends BaseParams> ProcessorResult processAndSave(
            Type paramsListType, MultistepProcessor<T> processor, 
            UUID id, String params) {

        List<T> steps = gson.fromJson(params, paramsListType);
        ProcessorResult result = processor.process(id, steps);
        processor.save(id, result);
        return result;
    }

}
