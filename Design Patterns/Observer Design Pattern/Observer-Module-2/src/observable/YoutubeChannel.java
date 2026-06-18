package observable;

import observer.Subscriber;

import java.util.List;
import java.util.ArrayList;

public class YoutubeChannel implements Channel {

    private final List<Subscriber> subscribers;
    private final String channelName;

    public YoutubeChannel(String channelName) {
        this.subscribers = new ArrayList<>();
        this.channelName = channelName;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        if (subscribers.contains(subscriber)) {
            System.out.println(subscriber.getUserName() + " is already subscribed to the channel '" + channelName + "'.");
            return;
        }
        subscribers.add(subscriber);
        System.out.println(subscriber.getUserName() + " subscribed to the channel '" + channelName + "'.");
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            System.out.println(subscriber.getUserName() + " is not subscribed to the channel '" + channelName + "'.");
            return;
        }
        subscribers.remove(subscriber);
        System.out.println(subscriber.getUserName() + " unsubscribed from the channel '" + channelName + "'.");
    }


    @Override
    public void uploadVideo(String videoTitle) {
        System.out.println("Video uploaded to the channel '" + channelName + "': " + videoTitle);
        notifySubscribers(videoTitle);
    }

    private void notifySubscribers(String videoTitle) {
        List<Subscriber> subscribersTempList = new ArrayList<>(subscribers);

        for (Subscriber subscriber : subscribersTempList) {
            subscriber.update(channelName, videoTitle);
        }
    }
}