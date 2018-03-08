package client;

import shared.request.RequestFonds;
import shared.interfaces.IFonds;
import shared.interfaces.IRequest;
import shared.sockets.ObjectInputConnection;
import shared.sockets.ObjectOutputConnection;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

class BannerController extends UnicastRemoteObject {

    private static final String DIVIDER = "     ";
    private static final int UPDATE_TIME = 2000;
    private final client.AEXBannerClient banner;
    private final Timer pollingTimer = new Timer();
    private Socket socket = null;
    private ObjectOutputConnection objectOutputConnection = null;
    private ObjectInputConnection objectInputConnection = null;

    public BannerController(client.AEXBannerClient banner) throws RemoteException {
        this.banner = banner;
        if (createConnections()) {
            startPollingTimer();
        }
    }

    public void stop() {
        pollingTimer.cancel();
        pollingTimer.purge();

        try{
            socket.close();
        } catch (IOException e) {
            System.out.println("Could not close socket");
        }
    }

    private void updateBanner() {
        List<IFonds> fondsen;

        //Uncomment to request 1 fonds
        IRequest request = new RequestFonds("Philips");

        //Uncomment to request 2 fondsen
        //List<String> fondsenToRequest = new ArrayList<String>();
        //fondsenToRequest.add("Aegon");
        //fondsenToRequest.add("Philips");
        //IRequest request = new RequestFondsen(fondsenToRequest);

        //Uncomment to request all fondsen
        //IRequest request = new RequestAllFondsen();

        objectOutputConnection.writeObject(request);
        Object result = objectInputConnection.readObject();
        fondsen = (ArrayList<IFonds>) result;

        if (fondsen != null && fondsen.size() > 0) {
            StringBuilder fondsenString = new StringBuilder();

            for (IFonds fonds : fondsen) {
                fondsenString.append(fonds.getNaam());
                fondsenString.append(": ");
                String koers = String.format("%.4f", fonds.getKoers());
                fondsenString.append(koers);
                fondsenString.append(DIVIDER);
            }

            banner.setKoersen(fondsenString.toString());
        }
    }

    private boolean createConnections()
    {
        if (createSocket()) {
            objectInputConnection = new ObjectInputConnection(socket);
            objectOutputConnection = new ObjectOutputConnection(socket);
        }

        boolean result = (objectInputConnection != null) && (objectOutputConnection != null);

        if (!result) {
            System.err.println("Failed to create connections.");
        }

        return result;
    }

    private boolean createSocket() {
        boolean result = true;

        try {
            socket = new Socket("127.0.0.1", 8189);
        } catch (IOException e) {
            System.err.println("Failed to create client socket.");
            result = false;
        }

        return result;
    }

    private void startPollingTimer() {
        pollingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateBanner();
            }
        }, 0, UPDATE_TIME);
    }
}
