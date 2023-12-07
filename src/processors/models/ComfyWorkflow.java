package processors.models;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ComfyWorkflow {
	private Map<String, Object> workflow;
	
	public ComfyWorkflow(Map<String, Object> workflow) {
		this.workflow = workflow;
	}
}