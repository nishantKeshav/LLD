public class LoggingHttpClientDecorator extends HttpClientDecorator{

    public LoggingHttpClientDecorator(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        System.out.println("LOG: Sending " + request.getMethod() + " to " + request.getUrl());
        System.out.println("LOG: Headers: " + request.getHeaders());
        HttpResponse response = super.execute(request);
        System.out.println("LOG: Received response status: " + response.getStatusCode());
        return response;
    }

}
