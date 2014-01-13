package tutorial;

import com.javax0.jdsl.executors.Factory;

public class AssignmentFactory implements Factory<Assignment> {
	@Override
	public Assignment get() {
		return new Assignment();
	}
}
