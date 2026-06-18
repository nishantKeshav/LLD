package observable;

import observer.Subscriber;

public interface Channel {
    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void notifySubscribers(String videoTitle);

    void uploadVideo(String videoTitle);
}
