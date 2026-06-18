import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RefundService {

    private final Map<RefundMode, RefundStrategy> refundStrategiesMap;

    public RefundService(List<RefundStrategy> refundStrategiesList) {
        refundStrategiesMap = new EnumMap<>(RefundMode.class);
        for (RefundStrategy refundStrategy : refundStrategiesList) {
            refundStrategiesMap.put(refundStrategy.getRefundMode() , refundStrategy);
        }
    }

    public void initiateRefund(RefundMode refundMode, RefundRequest refundRequest) {
        RefundRequestValidator refundRequestValidator = new RefundRequestValidator();
        refundRequestValidator.validateCommonFields(refundRequest);
        RefundStrategy refundStrategy = refundStrategiesMap.get(refundMode);

        if (refundStrategy == null) {
            throw new IllegalArgumentException("RefundStrategy not found for refundMode: " + refundMode);
        }

        refundStrategy.refund(refundRequest);
    }
}
