public class UnstableHttpClient implements HttpClient {

    private int attemptCount = 0;

    @Override
    public HttpResponse execute(HttpRequest request) {
        attemptCount++;

        if (attemptCount < 3) {
            throw new RuntimeException("Temporary external service failure");
        }

        System.out.println("Executing HTTP request after retries: "
                + request.getMethod()
                + " "
                + request.getUrl());

        return new HttpResponse(
                200,
                "Successful response after retries",
                true
        );
    }
}