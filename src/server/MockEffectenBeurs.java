package server;

import shared.Fonds;
import shared.interfaces.IEffectenBeurs;
import shared.interfaces.IFonds;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MockEffectenBeurs extends UnicastRemoteObject implements IEffectenBeurs {

    private static final int UPDATE_TIME = 4000;

    private List<IFonds> fondsen;
    private final Random random = new Random();
    private ServerSocketConnection serverSocketConnection = null;
    private final Timer updateTimer = new Timer();

    public MockEffectenBeurs() throws RemoteException {

        addFondsen();
        setTimer();
        createSockets();
        handleRequests();
    }

    @Override
    public List<IFonds> getKoersen() {
        return fondsen;
    }

    private void updateKoersen() {
        //System.out.println("Update koersen");

        for (IFonds fonds : fondsen) {
            boolean add = random.nextBoolean();
            double koers = fonds.getKoers();
            double difference = random.nextDouble() * (fonds.getKoers() / 10);
            double nieuweKoers = add ? koers + difference : koers - difference;
            Fonds f = (Fonds) fonds;
            f.setKoers(nieuweKoers);
        }
    }

    private void addFondsen() {
        fondsen = new ArrayList<>();
        fondsen.add(new Fonds("Aegon", 5.618));
        fondsen.add(new Fonds("KPN", 2.633));
        fondsen.add(new Fonds("Philips", 31.500));
        fondsen.add(new Fonds("Randstad", 58.360));
        fondsen.add(new Fonds("Unilever", 43.485));
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
        serverSocketConnection = new ServerSocketConnection();
    }

    private void handleRequests() {
        boolean waitForConnection = true;

        while (waitForConnection) {
            serverSocketConnection.accept();

            Runnable task2 = () -> {
                handleRequest();
            };

            Thread t = new Thread(task2);
            t.start();
        }
    }

    private void handleRequest() {
        System.out.println("Start handleRequest");

        while(true) {
            System.out.println("handleRequest");
            Object receivedCommand = serverSocketConnection.readObject();
            if ((receivedCommand != null) && receivedCommand.equals("getFondsen")) {
                System.out.println("getFondsen command received.");
                if (serverSocketConnection.writeObject(fondsen)) {
                    System.out.println("Sent: " + fondsen.get(0).getKoers());
                    System.out.println("Fondsen sent.");
                } else {
                    System.err.println("Unable to send fondsen.");
                }
            }
        }
    }
}
