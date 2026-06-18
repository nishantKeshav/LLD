public abstract class HttpClientDecorator implements HttpClient {

    private final HttpClient httpClient;

    protected HttpClientDecorator(HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null.");
        }
        this.httpClient = httpClient;
    }

    protected HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        return httpClient.execute(request);
    }
}
