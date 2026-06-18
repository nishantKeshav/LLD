import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PaymentService {
    Map<PaymentMode, PaymentStrategy> paymentStrategyMap;

    public PaymentService(List<PaymentStrategy> paymentStrategyList) {
        paymentStrategyMap = new EnumMap<>(PaymentMode.class);
        for (PaymentStrategy strategy : paymentStrategyList) {
            paymentStrategyMap.put(strategy.getPaymentMode(), strategy);
        }
    }

    public void makePayment(PaymentMode paymentMode, PaymentRequest paymentRequest) {
        PaymentStrategy strategy = paymentStrategyMap.get(paymentMode);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + paymentMode);
        }
        strategy.pay(paymentRequest);
    }
}
