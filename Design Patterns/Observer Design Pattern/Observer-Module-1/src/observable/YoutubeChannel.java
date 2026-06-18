package observable;

import observer.Subscriber;

import java.util.List;
import java.util.ArrayList;

public class YoutubeChannel implements Channel {

    private final String channelName;
    private final List<Subscriber> subscribers;

    public YoutubeChannel(String channelName) {
        this.channelName = channelName;
        this.subscribers = new ArrayList<>();
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
        System.out.println("Subscriber added: " + subscriber.getUserName());
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
        System.out.println("Subscriber removed: " + subscriber.getUserName());
    }

    @Override
    public void notifySubscribers(String videoTitle) {
        List<Subscriber> tempSubscribers = new ArrayList<>(subscribers);
        for (Subscriber subscriber : tempSubscribers) {
            subscriber.update(videoTitle);
        }
    }

    @Override
    public void uploadVideo(String videoTitle) {
        System.out.println(channelName + " uploaded new video: " + videoTitle);
        notifySubscribers(videoTitle);
    }

}
