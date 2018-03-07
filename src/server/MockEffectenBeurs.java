package server;

import shared.Fonds;
import shared.IEffectenBeurs;
import shared.IFonds;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MockEffectenBeurs extends UnicastRemoteObject implements IEffectenBeurs {

    private static final int UPDATE_TIME = 4000;

    private List<IFonds> fondsen;
    private final Random random = new Random();
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private final Timer updateTimer = new Timer();
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

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
        System.out.println("Update koersen");

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

    private boolean createSockets() {
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

        if (result) {
            try {
                outputStream = clientSocket.getOutputStream();
            } catch (IOException e) {
                System.err.println("Failed to create output stream.");
                result = false;
            }
        }

        if (result) {
            try {
                inputStream = clientSocket.getInputStream();
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
                objectOutputStream = new ObjectOutputStream(outputStream);
            } catch (IOException e) {
                System.err.println("Failed to create object output stream.");
                result = false;
            }
        }

        if (result) {
            System.out.println("createSockets completed successfully");
        }

        return result;
    }

    private void handleRequests() {
        boolean handeRequests = true;
        Object receivedCommand = null;

        while (handeRequests) {
            try {
                receivedCommand = objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Unknown object type used.");
            }

            if (receivedCommand != null) {
                if (receivedCommand.equals("getFondsen")) {
                    try {
                        System.out.println("getFondsen command received.");
                        objectOutputStream.reset(); // Needed to stop caching!
                        objectOutputStream.writeObject(fondsen);
                        objectOutputStream.flush();

                        System.out.println("Sent: " + fondsen.get(0).getKoers());
                        System.out.println("Fondsen sent.");
                    } catch (IOException e) {
                        System.err.println("Unable to send fondsen.");
                    }
                }
            }
        }
    }

}
