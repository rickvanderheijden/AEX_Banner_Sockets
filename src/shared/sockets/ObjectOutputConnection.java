package shared.sockets;

import java.io.*;
import java.net.Socket;

public class ObjectOutputConnection {

    private final Socket socket;
    private ObjectOutputStream objectOutputStream = null;
    public ObjectOutputConnection(Socket socket) {
        this.socket = socket;

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

    public void writeObject(Object object) {
        if (objectOutputStream != null) {
            try {
                objectOutputStream.reset(); // Needed to prevent caching!
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
            } catch (IOException e) {
                System.err.println("Failed to write object to output stream.");
                closeSocket();
            }
        }
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket.");
        }
    }
}
