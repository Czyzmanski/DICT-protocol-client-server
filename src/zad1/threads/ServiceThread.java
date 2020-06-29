package zad1.threads;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public abstract class ServiceThread extends Thread {
    protected Socket clientSocket;
    
    @SuppressWarnings("rawtypes")
    protected Map map;
    
    protected InputStream in;
    protected BufferedReader reader;

    @SuppressWarnings("rawtypes")
    public ServiceThread(Socket clientSocket, Map map) throws IOException {
        this.clientSocket = clientSocket;
        this.map = map;
        this.in = clientSocket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
    }
    
    @Override
    public abstract void run();
    
    public void dispose() throws IOException {
        reader.close();
        clientSocket.close();
    }

}
