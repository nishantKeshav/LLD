public class AuthHeaderHttpClientDecorator extends HttpClientDecorator {

    private final String token;

    public AuthHeaderHttpClientDecorator(HttpClient httpClient, String token) {
        super(httpClient);

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Auth token cannot be null or blank.");
        }

        this.token = token;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        request.addHeader("Authorization", token);
        System.out.println("AUTH: Authorization header added");
        return super.execute(request);
    }
}