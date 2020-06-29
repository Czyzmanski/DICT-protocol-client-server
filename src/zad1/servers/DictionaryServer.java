package zad1.servers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zad1.threads.DelegationThread;
import zad1.threads.ListeningThread;
import zad1.threads.RegistrationThread;

public class DictionaryServer extends Server {
    private final int SERVICE_PORT = 9004;
    private final int REGISTRATION_PORT = 2004;

    private ServerSocket translationSocket;
    private ServerSocket registrationSocket;

    private ListeningThread registrationListeningThread;
    private ListeningThread translationListeningThread;

    private Map<String, InetSocketAddress> languageServersMap;

    private DictionaryServer() throws IOException {
        languageServersMap = new ConcurrentHashMap<>();
        translationSocket = new ServerSocket(SERVICE_PORT);
        registrationSocket = new ServerSocket(REGISTRATION_PORT);
        registrationListeningThread = new ListeningThread(registrationSocket, languageServersMap,
                RegistrationThread.class);
        translationListeningThread = new ListeningThread(translationSocket, languageServersMap,
                DelegationThread.class);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    registrationListeningThread.interrupt();
                    translationListeningThread.interrupt();
                    translationSocket.close();
                    registrationSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void work() {
        registrationListeningThread.start();
        translationListeningThread.start();
    }

    public static void main(String[] args) {
        try {
            new DictionaryServer().work();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
