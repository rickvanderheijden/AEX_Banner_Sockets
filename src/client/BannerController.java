package client;

import shared.IFonds;

import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class BannerController extends UnicastRemoteObject {

    private static final String DIVIDER = "     ";
    private static final int UPDATE_TIME = 2000;
    private final client.AEXBannerClient banner;
    private final Timer pollingTimer = new Timer();
    private Socket socket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    public BannerController(client.AEXBannerClient banner) throws RemoteException {
        this.banner = banner;
        createSockets();
        startPollingTimer();
    }

    public void stop() {
        pollingTimer.cancel();
        pollingTimer.purge();
    }

    private void updateBanner() {
        List<IFonds> fondsen = null;
        String command = "getFondsen";

        try {
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            Object result = objectInputStream.readObject();
            fondsen = (ArrayList<IFonds>) result;
            System.out.println("fondsen received.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Unknown object type used.");
        }

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

    private boolean createSockets() {
        boolean result = true;

        try {
            socket = new Socket("127.0.0.1", 8189);
        } catch (IOException e) {
            System.err.println("Failed to create client socket.");
            result = false;
        }

        if (result) {
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                System.err.println("Failed to create input stream.");
                result = false;
            }
        }

        if (result) {
            try {
                objectInputStream = new ObjectInputStream(inputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object input stream.");
                result = false;
            }
        }

        if (result) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                System.err.println("Failed to create output stream.");
                result = false;
            }
        }

        if (result) {
            try {
                objectOutputStream = new ObjectOutputStream(outputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object output stream.");
                result = false;
            }
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
