import java.io.Serializable;

public class Message implements Serializable {

    int source;
    long time;
    int name;
    String type;

    public Message(String type, int source, long time) {
        this.source = source;
        this.time = time;
        this.type = type;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

