package server;

import shared.interfaces.IEffectenBeurs;

import java.rmi.RemoteException;

class AEXBannerServer {
    public static void main(String[] args) throws RemoteException {

        // Welcome message
        System.out.println("AEX Banner Service is running");

        // Create effectenbeurs
        IEffectenBeurs effectenBeurs = new MockEffectenBeurs();

        System.out.println("AEX Banner Service stopped running");
    }
}
