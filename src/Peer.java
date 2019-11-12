import java.io.Serializable;

public class Peer implements Serializable {
    private String hostName;
    private String hostUserName;
    private String speed;
    private String ipAddress;

    //constructor which creates the actual PeerData object. holds info for file.
    public Peer(String hostUserName, String hostName, String speed, String ipAddress) {
        this.hostUserName = hostUserName;
        this.hostName = hostName;
        this.speed = speed;
        this.ipAddress = ipAddress;
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

    public String getIpAddress() {
        return ipAddress;
    }
}
