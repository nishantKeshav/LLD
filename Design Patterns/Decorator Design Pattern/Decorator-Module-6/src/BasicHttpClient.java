public class BasicHttpClient implements HttpClient {

    @Override
    public HttpResponse execute(HttpRequest request) {
        System.out.println("Executing HTTP request: " + request.getMethod() + " " + request.getUrl());
        return new HttpResponse(200, "Response from " + request.getUrl(), true);
    }
}
