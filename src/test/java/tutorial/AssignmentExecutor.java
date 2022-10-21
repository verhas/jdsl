package tutorial;

import com.javax0.jdsl.executors.AbstractListExecutor;
import com.javax0.jdsl.executors.Context;
import com.javax0.jdsl.executors.TerminalSymbolExecutor;

public class AssignmentExecutor extends AbstractListExecutor {

    @Override
    public Object execute(Context context) {
        final var identifierExecutor = getExecutor(0);
        if (identifierExecutor instanceof TerminalSymbolExecutor) {
            String identifier = ((TerminalSymbolExecutor<String>) identifierExecutor).execute(context);
            Object expressionValue = getExecutor(1).execute(context);
            ((SimpleInterpreterContext) context).put(identifier, expressionValue);
            return null;
        } else {
            throw new RuntimeException("Identifier executor is not terminal symbol executor");
        }
    }
    public static class Factory implements com.javax0.jdsl.executors.Factory<AssignmentExecutor> {
        @Override
        public AssignmentExecutor get() {
            return new AssignmentExecutor();
        }
    }

}
