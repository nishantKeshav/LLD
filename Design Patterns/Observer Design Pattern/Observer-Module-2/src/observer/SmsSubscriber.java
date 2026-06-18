package observer;

public class SmsSubscriber implements Subscriber {

    private final String userName;
    private final String mobileNumber;

    public SmsSubscriber(String userName, String mobileNumber) {
        this.userName = userName;
        this.mobileNumber = mobileNumber;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("SMS sent to " + mobileNumber);
        System.out.println("SMS sent to " + mobileNumber + ": New video '" + videoTitle + "' uploaded on channel '" + channelName + "'.");
    }

    @Override
    public String getUserName() {
        return this.userName;
    }
}
