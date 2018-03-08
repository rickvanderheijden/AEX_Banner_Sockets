package shared.sockets;

import java.io.*;
import java.net.Socket;

public class ObjectOutputConnection {

    private ObjectOutputStream objectOutputStream = null;
    public ObjectOutputConnection(Socket socket) {

        OutputStream outputStream = null;
        if (socket != null) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                System.err.println("Failed to create output stream.");
            }
        }

        if (outputStream != null) {
            try {
                objectOutputStream = new ObjectOutputStream(outputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object output stream.");
            }
        }
    }

    public boolean writeObject(Object object) {
        boolean result = true;
        if (objectOutputStream != null) {
            try {
                objectOutputStream.reset(); // Needed to prevent caching!
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }

        return result;
    }
}
