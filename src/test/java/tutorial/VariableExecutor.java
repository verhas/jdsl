package tutorial;

import com.javax0.jdsl.executors.AbstractListExecutor;
import com.javax0.jdsl.executors.Context;
import com.javax0.jdsl.executors.ListExecutor;

public class VariableExecutor extends AbstractListExecutor {
    @Override
    public Object execute(final Context context) {
        if( numberOfExecutors() != 1 ){
            throw new RuntimeException("There is some problem...");
        }
        final var variableName = (String)getExecutor(0).execute(context);
        return ((SimpleInterpreterContext)context).get(variableName);
    }

    public static class Factory implements com.javax0.jdsl.executors.Factory<ListExecutor> {
        @Override
        public VariableExecutor get() {
            return new VariableExecutor();
        }
    }
}
