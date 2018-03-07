package shared.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ObjectInputConnection {

    private InputStream inputStream = null;
    private ObjectInputStream objectInputStream = null;

    public ObjectInputConnection(Socket socket) {

        if (socket != null) {
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                System.err.println("Failed to create input stream.");
            }
        }

        if (inputStream != null) {
            try {
                objectInputStream = new ObjectInputStream(inputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object input stream.");
            }
        }
    }

    public Object readObject() {
        Object result = null;
        if (objectInputStream != null) {
            try {
                result = objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
