package server;

import shared.interfaces.IFonds;
import shared.sockets.ObjectInputConnection;
import shared.sockets.ObjectOutputConnection;

import java.net.Socket;
import java.util.List;

class RequestHandler implements Runnable {
    private final ObjectOutputConnection objectOutputConnection;
    private final ObjectInputConnection objectInputConnection;
    private final List<IFonds> fondsen;

    public RequestHandler(Socket clientSocket, List<IFonds> fondsen) {
        objectOutputConnection = new ObjectOutputConnection(clientSocket);
        objectInputConnection = new ObjectInputConnection(clientSocket);
        this.fondsen = fondsen;
    }

    @Override
    public void run() {
        boolean handleRequests = true;
        Object receivedCommand;

        while (handleRequests) {
            receivedCommand = objectInputConnection.readObject();
            if ((receivedCommand != null) && receivedCommand.equals("getFondsen")) {
                if (objectOutputConnection.writeObject(fondsen)) {
                    System.out.println("Fondsen sent.");
                } else {
                    System.err.println("Unable to send fondsen.");
                }
            }
        }
    }
}
