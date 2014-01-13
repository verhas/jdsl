package tutorial;

import java.util.HashMap;
import java.util.Map;

import com.javax0.jdsl.executors.Context;

public class SimpleInterpreterContext implements Context {
	private final Map<String, Object> variables = new HashMap<>();

	public void put(String key, Object value) {
		variables.put(key, value);
	}

	public Object get(String key) {
		return variables.get(key);
	}
}
