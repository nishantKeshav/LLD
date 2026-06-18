import java.util.Map;
import java.util.HashMap;

public class HttpRequest {

    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final int simulatedDelayMs;

    public HttpRequest(String url, String method, Map<String, String> headers, String body, int simulatedDelayMs) {
        this.url = url;
        this.method = method;
        this.headers = headers == null ? new HashMap<>() : new HashMap<>(headers);
        this.body = body;
        this.simulatedDelayMs = simulatedDelayMs;
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