package observer;

public class UserSubscriber implements Subscriber {

    private final String userName;

    public UserSubscriber(String userName) {
        this.userName = userName;
    }

    @Override
    public void update(String videoTitle) {
        System.out.println(userName + " got notification for new video: " + videoTitle);
    }

    @Override
    public String getUserName() {
        return userName;
    }

}
