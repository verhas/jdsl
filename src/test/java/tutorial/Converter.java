package tutorial;

public class Converter {
    public static double toDouble(final Number op1) {
        final double dOp1;
        if (op1 instanceof Long) {
            dOp1 = 0.0 + (Long) op1;
        } else {
            dOp1 = (double) op1;
        }
        return dOp1;
    }
}
