package observer;

public class EmailSubscriber implements Subscriber {

    private final String userName;
    private final String email;

    public EmailSubscriber(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("EMAIL sent to " + email);
        System.out.println("Email sent to " + email + ": New video '" + videoTitle + "' uploaded on channel '" + channelName + "'.");
    }

    @Override
    public String getUserName() {
        return userName;
    }
}
