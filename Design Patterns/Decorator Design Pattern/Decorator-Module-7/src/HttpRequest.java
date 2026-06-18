import java.util.Map;
import java.util.HashMap;

public class HttpRequest {

    private final String url;
    private final String body;
    private final String method;
    private final int simulatedDelayMs;
    private final Map<String, String> headers;

    public HttpRequest(String url, String method, Map<String, String> headers, String body, int simulatedDelayMs) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank.");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("HTTP method cannot be null or blank.");
        }
        if (simulatedDelayMs < 0) {
            throw new IllegalArgumentException("Simulated delay cannot be negative.");
        }
        this.url = url;
        this.body = body;
        this.method = method;
        this.simulatedDelayMs = simulatedDelayMs;
        this.headers = headers == null ? new HashMap<>() : new HashMap<>(headers);
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public String getBody() {
        return body;
    }

    public int getSimulatedDelayMs() {
        return simulatedDelayMs;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}