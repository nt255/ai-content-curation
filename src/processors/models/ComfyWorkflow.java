package processors.models;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Builder
@Getter
public class ComfyWorkflow {
	private Map<String, Object> workflow;
	
	public ComfyWorkflow(Map<String, Object> workflow) {
		this.workflow = workflow;
	}
	
	@Override
    public String toString() {
        return workflow != null ? workflow.toString() : "ComfyWorkflow is null";
    }
}