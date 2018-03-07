package sockets;

import shared.IFonds;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketConnection {
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private ObjectOutputConnection objectOutputConnection = null;
    private ObjectInputConnection objectInputConnection = null;

    public ServerSocketConnection() {

        boolean result = true;

        try {
            serverSocket = new ServerSocket(8189);
        } catch (IOException e) {
            System.err.println("Failed to create server socket.");
            result = false;
        }

        if (result) {
            try {
                System.out.println("Waiting for incoming connection");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                result = false;
            }
        }

        objectOutputConnection = new ObjectOutputConnection(clientSocket);
        objectInputConnection = new ObjectInputConnection(clientSocket);


        if (result) {
            System.out.println("Sockets completed successfully");
        }
    }

    public Object readObject() {
        return objectInputConnection.readObject();
    }

    public boolean writeObject(Object object) {
        return objectOutputConnection.writeObject(object);
    }
}
