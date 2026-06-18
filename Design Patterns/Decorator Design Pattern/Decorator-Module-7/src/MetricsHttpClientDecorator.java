public class MetricsHttpClientDecorator extends HttpClientDecorator {

    private final HttpClientMetrics metrics;

    public MetricsHttpClientDecorator(HttpClient httpClient, HttpClientMetrics metrics) {
        super(httpClient);

        if (metrics == null) {
            throw new IllegalArgumentException("HttpClientMetrics cannot be null.");
        }

        this.metrics = metrics;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        metrics.incrementTotalRequests();

        try {
            HttpResponse response = super.execute(request);

            if (response.isSuccess()) {
                metrics.incrementSuccessResponses();
            } else {
                metrics.incrementFailedResponses();
            }

            System.out.println("METRICS: HTTP metrics updated");

            return response;

        } catch (Exception e) {
            metrics.incrementFailedResponses();
            System.out.println("METRICS: HTTP metrics updated after failure");
            throw e;
        }
    }
}