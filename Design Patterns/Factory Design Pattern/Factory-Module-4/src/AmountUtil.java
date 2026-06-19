public class AmountUtil {

    private AmountUtil() {
        /* This utility class should not be instantiated */
    }

    public static double calculateTotalAmount(ReportRequest request) {
        return request.getRows()
                .stream()
                .mapToDouble(ReportRow::getAmount)
                .sum();
    }
}
