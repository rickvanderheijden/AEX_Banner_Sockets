package server;

import shared.sockets.ObjectInputConnection;
import shared.sockets.ObjectOutputConnection;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketConnection {
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ObjectOutputConnection objectOutputConnection = null;
    private ObjectInputConnection objectInputConnection = null;

    public ServerSocketConnection() {
        try {
            serverSocket = new ServerSocket(8189);
        } catch (IOException e) {
            System.err.println("Failed to create server socket.");
        }
    }

    public void accept() {
        try {
            System.out.println("Waiting for incoming connection");
            clientSocket = serverSocket.accept();
            objectOutputConnection = new ObjectOutputConnection(clientSocket);
            objectInputConnection = new ObjectInputConnection(clientSocket);
            System.out.println("Accepted incoming connection");
        } catch (IOException e) {
            System.err.println("Accept failed.");
        }
    }

    public Object readObject() {
        return objectInputConnection.readObject();
    }

    public boolean writeObject(Object object) {
        return objectOutputConnection.writeObject(object);
    }
}
