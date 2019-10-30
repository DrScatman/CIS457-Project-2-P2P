public class Peer {
    private String hostName;
    private String hostUserName;
    private String speed;

    //constructor which creates the actual PeerData object. holds info for file.
    public Peer(String hostUserName, String hostName, String speed) {
        this.hostUserName = hostUserName;
        this.hostName = hostName;
        this.speed = speed;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostUserName() {
        return hostUserName;
    }

    public String getSpeed() {
        return speed;
    }
}
