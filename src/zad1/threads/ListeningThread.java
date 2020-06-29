package zad1.threads;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ListeningThread extends Thread {
    private ServerSocket listeningSocket;
    private Map<?, ?> map;
    private Class<? extends ServiceThread> serviceThreadClass;

    public ListeningThread(ServerSocket listeningSocket, Map<?, ?> map,
            Class<? extends ServiceThread> serviceThreadClass) {
        this.listeningSocket = listeningSocket;
        this.map = map;
        this.serviceThreadClass = serviceThreadClass;
    }

    @Override
    public void run() {
        while (Thread.currentThread().isInterrupted() == false) {
            try {
                Socket clientSocket = listeningSocket.accept();
                ServiceThread serviceThread = serviceThreadClass.getConstructor(Socket.class, Map.class)
                        .newInstance(clientSocket, map);
                serviceThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
