package tutorial;

import com.javax0.jdsl.executors.AbstractListExecutor;
import com.javax0.jdsl.executors.Context;
import com.javax0.jdsl.executors.TerminalSymbolExecutor;

public class Assignment extends AbstractListExecutor {

	@Override
	public Object execute(Context context) {
		String identifier = ((TerminalSymbolExecutor<String>) getExecutor(0))
				.execute(context);
		Object expressionValue = getExecutor(1).execute(context);
		((SimpleInterpreterContext) context).put(identifier, expressionValue);
		return null;
	}

}
