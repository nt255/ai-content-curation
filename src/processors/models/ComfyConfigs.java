package processors.models; 

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ComfyConfigs {
	private Map<String, String> node;
	
	public boolean containsKey(String key) {
		return node.containsKey(key);
	}
	
	public Object get(String key) {
		return node.get(key);
	}
}