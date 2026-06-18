package observer;

public class PushSubscriber implements Subscriber {

    private final String userName;
    private final String deviceToken;

    public PushSubscriber(String userName, String deviceToken) {
        this.userName = userName;
        this.deviceToken = deviceToken;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("Push notification sent to " + userName + " on device Token '" + deviceToken + "'.");
        System.out.println("Push notification sent to " + userName + ": New video '" + videoTitle + "' uploaded on channel '" + channelName + "'.");
    }

    @Override
    public String getUserName() {
        return this.userName;
    }
}
