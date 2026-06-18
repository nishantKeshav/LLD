import observer.EmailSubscriber;
import observer.PushSubscriber;
import observer.SmsSubscriber;
import observer.Subscriber;
import observable.Channel;
import observable.YoutubeChannel;

public class Main {
    public static void main(String[] args) {
        System.out.println("Observer Design Pattern - YouTube Modified");

        Channel channel = new YoutubeChannel("CodeWithMyD");

        Subscriber user1 = new EmailSubscriber("Amit", "amit@gmail.com");
        Subscriber user2 = new SmsSubscriber("Priya", "9876543210");
        Subscriber user3 = new PushSubscriber("Rahul", "device-token-123");

        channel.subscribe(user1);
        channel.subscribe(user2);
        channel.subscribe(user3);

        // duplicate subscription test
        channel.subscribe(user1);

        channel.uploadVideo("Observer Pattern Module 2");

        channel.unsubscribe(user2);

        channel.uploadVideo("Observer Pattern Module 2 - Part 2");

        // invalid unsubscribe test
        channel.unsubscribe(user2);

    }
}