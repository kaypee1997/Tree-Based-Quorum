import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {

	@Override
	public int compare(Message m1, Message m2) {
		// TODO Auto-generated method stub
		int faceID;
		if (m1.getTime() < m2.getTime()) {
            return -1;
        }
        if (m1.getTime() > m2.getTime()) {
            return 1;
        }
        if (m1.getSource() < m2.getSource()) {
            return -1;
        }
        if (m1.getSource() > m2.getSource()) {
            return 1;
        }
		return 0;
	}

}
