package zad1.threads;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

public class RegistrationThread extends ServiceThread {

	public RegistrationThread(Socket clientSocket, Map<String, InetSocketAddress> languageServersMap) throws IOException {
		super(clientSocket, languageServersMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			String[] registration = reader.readLine().trim().split("\\s+");
			String languageServerName = registration[0].toUpperCase();
			String hostAddress = clientSocket.getInetAddress().getHostAddress();
			int portNumber = Integer.parseInt(registration[1]);
			
			InetSocketAddress languageServerSocket = new InetSocketAddress(hostAddress, portNumber);
			map.put(languageServerName, languageServerSocket);
			
			System.out.println("REGISTRATION");
			System.out.println(map);
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

}
