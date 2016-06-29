package client;

import java.io.DataOutputStream;
import java.io.IOException;

public interface GenericSender {
	abstract public void writeToDataStream(DataOutputStream dos) throws IOException;
}
