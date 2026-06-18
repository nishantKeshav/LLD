import java.util.UUID;

public class RequestIdHttpClientDecorator extends HttpClientDecorator {

    public RequestIdHttpClientDecorator(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        String requestId = UUID.randomUUID().toString();
        request.addHeader("X-Request-Id", requestId);
        System.out.println("REQUEST-ID: Added request id " + requestId);
        return super.execute(request);
    }
}