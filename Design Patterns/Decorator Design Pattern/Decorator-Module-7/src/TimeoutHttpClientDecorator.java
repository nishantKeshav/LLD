public class TimeoutHttpClientDecorator extends HttpClientDecorator {

    private final int timeoutMs;

    public TimeoutHttpClientDecorator(HttpClient httpClient, int timeoutMs) {
        super(httpClient);
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("Timeout must be greater than zero.");
        }
        this.timeoutMs = timeoutMs;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        if (request.getSimulatedDelayMs() > timeoutMs) {
            System.out.println("TIMEOUT: Request timed out before execution");
            return new HttpResponse(
                    408,
                    "Request timed out",
                    false
            );
        }
        System.out.println("TIMEOUT: Request within timeout limit");
        return super.execute(request);
    }
}