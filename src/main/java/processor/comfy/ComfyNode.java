package main.java.processor.comfy; 

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ComfyNode {
	private Map<String, Object> node;
	
	public Object get(String key) {
		return node.get(key);
	}
	
	public void put(String key, Object value) {
		node.put(key, value);
	}
}