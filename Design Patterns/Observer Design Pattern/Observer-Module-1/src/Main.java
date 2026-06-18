import observable.Channel;
import observer.UserSubscriber;
import observable.YoutubeChannel;

void main() {
    System.out.println(String.format("Observer Design Pattern - YouTube"));

    Channel youtubeChannel = new YoutubeChannel("CodeWithMyD");

    UserSubscriber user1 = new UserSubscriber("Nishant 1");
    UserSubscriber user2 = new UserSubscriber("Nishant 2");
    UserSubscriber user3 = new UserSubscriber("Nishant 3");

    youtubeChannel.subscribe(user1);
    youtubeChannel.subscribe(user2);
    youtubeChannel.subscribe(user3);

    youtubeChannel.uploadVideo("Observer Design Pattern in Java PART-1");

    youtubeChannel.unsubscribe(user1);
    youtubeChannel.unsubscribe(user3);

    youtubeChannel.uploadVideo("Observer Design Pattern in Java PART-2");

}
