package processors.clients;

import java.nio.file.Path;
import java.util.Properties;

import com.google.inject.Inject;
import com.hexadevlabs.gpt4all.LLModel;

import lombok.Getter;

@Getter
public class GPT4AllLoader {
    
    private final LLModel defaultModel;
    
    @Inject
    public GPT4AllLoader(Properties properties) {
        String modelFilePath = 
                properties.getProperty("resources.root") +
                properties.getProperty("resources.gpt4all.model.default");
        
        this.defaultModel = new LLModel(Path.of(modelFilePath));
    }

}