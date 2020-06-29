package zad1.threads;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class DelegationThread extends ServiceThread {

    public DelegationThread(Socket clientSocket, Map<String, InetSocketAddress> languageServersMap) throws IOException {
        super(clientSocket, languageServersMap);
    }

    @Override
    public void run() {
        try {
            String[] request = reader.readLine().split("\\s+");
            String wordToTransl = request[0];
            String clientHostAddress = clientSocket.getInetAddress().getHostAddress();
            int clientPort = Integer.parseInt(request[2]);
            InetSocketAddress languageServerSocket = (InetSocketAddress) map.get(request[1].toUpperCase());

            if (languageServerSocket == null)
                reportNoLanguage(clientHostAddress, clientPort);
            else
                delegateRequest(wordToTransl, clientHostAddress, clientPort, languageServerSocket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void delegateRequest(String wordToTransl, String clientHostAddress, int clientPort,
            InetSocketAddress languageServerSocket) throws IOException {
        Socket delegationSocket = null;
        BufferedWriter writer = null;
        try {
            delegationSocket = new Socket(languageServerSocket.getHostString(), languageServerSocket.getPort());
            writer = new BufferedWriter(
                    new OutputStreamWriter(new BufferedOutputStream(delegationSocket.getOutputStream())));
            writer.write(String.format("%s %s %d%n", wordToTransl, clientHostAddress, clientPort));
        } finally {
            if (writer != null)
                writer.close();
            if (delegationSocket != null)
                delegationSocket.close();
        }
    }
    
    protected void reportNoLanguage(String clientHostAddress, int clientPort) throws IOException {
        Socket clientReceivingSocket = null;
        BufferedWriter writer = null;
        try {
            clientReceivingSocket = new Socket(clientHostAddress, clientPort);
            writer = new BufferedWriter(
                    new OutputStreamWriter(new BufferedOutputStream(clientReceivingSocket.getOutputStream())));
            writer.write("Language not available." + System.lineSeparator());
        } finally {
            if (writer != null)
                writer.close();
            if (clientReceivingSocket != null)
                clientReceivingSocket.close();
        }
    }

}
