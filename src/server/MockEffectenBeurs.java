package server;

import shared.Fonds;
import shared.interfaces.IEffectenBeurs;
import shared.interfaces.IFonds;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MockEffectenBeurs extends UnicastRemoteObject implements IEffectenBeurs {

    private static final int UPDATE_TIME = 4000;

    private List<IFonds> fondsen;
    private final Random random = new Random();
    private ServerSocket serverSocket = null;
    private final Timer updateTimer = new Timer();

    public MockEffectenBeurs() throws RemoteException {

        addFondsen();
        setTimer();
        createSockets();
        handleRequests();
    }

    private void addFondsen() {
        fondsen = new ArrayList<>();
        fondsen.add(new Fonds("Aegon", 5.618));
        fondsen.add(new Fonds("KPN", 2.633));
        fondsen.add(new Fonds("Philips", 31.500));
        fondsen.add(new Fonds("Randstad", 58.360));
        fondsen.add(new Fonds("Unilever", 43.485));
    }

    @Override
    public List<IFonds> getKoersen() {
        return fondsen;
    }

    private void updateKoersen() {
        for (IFonds fonds : fondsen) {
            boolean add = random.nextBoolean();
            double koers = fonds.getKoers();
            double difference = random.nextDouble() * (fonds.getKoers() / 10);
            double nieuweKoers = add ? koers + difference : koers - difference;
            Fonds f = (Fonds) fonds;
            f.setKoers(nieuweKoers);
        }
    }

    private void setTimer() {
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateKoersen();
            }
        }, 0, UPDATE_TIME);
    }

    private void createSockets() {
        try {
            serverSocket = new ServerSocket(8189);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequests() {
        boolean waitForConnection = true;

        while (waitForConnection) {
            try {
                Thread requestHandlerThread = new Thread(new RequestHandler(serverSocket.accept(), getKoersen()));
                requestHandlerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
