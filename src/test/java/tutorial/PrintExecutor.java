package tutorial;

import com.javax0.jdsl.executors.AbstractListExecutor;
import com.javax0.jdsl.executors.Context;

public class PrintExecutor extends AbstractListExecutor {

    @Override
    public Object execute(final Context context) {
        if (numberOfExecutors() != 1) {
            throw new RuntimeException("There has to be an expression to print. This must not happen here.");
        }
        System.out.printf("%s%n", getExecutor(0).execute(context));
        return null;
    }

    public static class Factory implements com.javax0.jdsl.executors.Factory<PrintExecutor> {
        @Override
        public PrintExecutor get() {
            return new PrintExecutor();
        }
    }
}
