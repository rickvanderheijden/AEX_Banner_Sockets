package shared.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ObjectInputConnection {

    private final Socket socket;
    private ObjectInputStream objectInputStream = null;

    public ObjectInputConnection(Socket socket) {
        this.socket = socket;

        InputStream inputStream = null;
        if (socket != null) {
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                System.err.println("Failed to create input stream.");
                closeSocket();
            }
        }

        if (inputStream != null) {
            try {
                objectInputStream = new ObjectInputStream(inputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object input stream.");
                closeSocket();
            }
        }
    }

    public Object readObject() {
        Object result = null;
        if (objectInputStream != null) {
            try {
                result = objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to read object from input stream.");
                closeSocket();
            }
        }

        return result;
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket.");
        }
    }
}
