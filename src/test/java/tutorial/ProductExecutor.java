package tutorial;

import com.javax0.jdsl.executors.AbstractListExecutor;
import com.javax0.jdsl.executors.Context;

public class ProductExecutor extends AbstractListExecutor {
    @Override
    public Object execute(Context context) {
        if (numberOfExecutors() == 1) {
            return getExecutor(0).execute(context);
        }
        final var op1 = (Number) getExecutor(0).execute(context);
        final var operator = (String) getExecutor(1).execute(context);
        final var op2 = (Number) getExecutor(2).execute(context);
        if (op1 instanceof Long && op2 instanceof Long) {
            if (operator.equals("*")) {
                return (Long) op1 * (Long) op2;
            } else {
                return (Long) op1 / (Long) op2;
            }

        }
        final double dOp1 = Converter.toDouble(op1);
        final double dOp2 = Converter.toDouble(op2);
        if (operator.equals("*")) {
            return dOp1 * dOp2;
        } else {
            return dOp1 / dOp2;
        }
    }

    public static class Factory implements com.javax0.jdsl.executors.Factory<ProductExecutor> {
        @Override
        public ProductExecutor get() {
            return new ProductExecutor();
        }
    }
}
