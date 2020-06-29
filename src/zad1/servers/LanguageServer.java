package zad1.servers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import zad1.threads.ListeningThread;
import zad1.threads.TranslationThread;

public class LanguageServer extends Server {
    private String serverName;
    private Map<String, String> translationMap;
    private InetSocketAddress regServerSocket;
    private ServerSocket translationSocket;
    private ListeningThread listeningThread;

    protected LanguageServer(String fileName, InetSocketAddress regServerSocket)
            throws FileNotFoundException, IOException {
        this.serverName = fileName.substring(0, fileName.lastIndexOf("."));
        this.translationMap = new HashMap<>();
        this.regServerSocket = regServerSocket;
        this.translationSocket = new ServerSocket(0);
        this.listeningThread = new ListeningThread(translationSocket, translationMap, TranslationThread.class);
        
        readFile(fileName);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                listeningThread.interrupt();
                translationSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private void readFile(String fileName) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":\\s+");
                translationMap.put(tokens[0], tokens[1]);
            }
        }
    }

    @Override
    protected void work() {
        try {
            register();
            listeningThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register() throws UnknownHostException, IOException {
        try (Socket registerSocket = new Socket(regServerSocket.getHostName(), regServerSocket.getPort());
                BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(new BufferedOutputStream(registerSocket.getOutputStream())))) {
            System.out.println(serverName);
            bw.write(String.format("%s %d%n", serverName, translationSocket.getLocalPort()));
            bw.flush();
        }
    }

    public static void main(String[] args) {
        try {
            InetSocketAddress regServerSocket = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
            new LanguageServer(args[0], regServerSocket).work();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
