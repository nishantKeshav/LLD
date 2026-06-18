package observer;

public interface Subscriber {
    void update(String videoTitle);
    String getUserName();
}