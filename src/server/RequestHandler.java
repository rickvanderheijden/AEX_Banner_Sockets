package server;

import shared.request.RequestAllFondsen;
import shared.request.RequestFonds;
import shared.request.RequestFondsen;
import shared.interfaces.IFonds;
import shared.interfaces.IRequest;
import shared.sockets.ObjectInputConnection;
import shared.sockets.ObjectOutputConnection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class RequestHandler implements Runnable {
    private final ObjectOutputConnection objectOutputConnection;
    private final ObjectInputConnection objectInputConnection;
    private final List<IFonds> fondsen;
    private final Socket clientSocket;

    public RequestHandler(Socket clientSocket, List<IFonds> fondsen) {
        objectOutputConnection = new ObjectOutputConnection(clientSocket);
        objectInputConnection = new ObjectInputConnection(clientSocket);
        this.clientSocket = clientSocket;
        this.fondsen = fondsen;
    }

    @Override
    public void run() {
        Object receivedCommand;
        while (!clientSocket.isClosed()) {
            receivedCommand = objectInputConnection.readObject();
            if (receivedCommand != null) {
                if (receivedCommand instanceof RequestAllFondsen) {
                    objectOutputConnection.writeObject(fondsen);
                } else if (receivedCommand instanceof RequestFondsen || receivedCommand instanceof RequestFonds) {
                    List<IFonds> fondsenToSend = new ArrayList<>();
                    for (IFonds fonds : fondsen) {
                        for (String requestedFonds : ((IRequest) receivedCommand).getRequestFondsen()) {
                            if (requestedFonds.equals(fonds.getNaam())) {
                                fondsenToSend.add(fonds);
                            }
                        }
                    }
                    objectOutputConnection.writeObject(fondsenToSend);
                }
            }
        }
    }
}
