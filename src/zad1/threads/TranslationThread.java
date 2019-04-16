package zad1.threads;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

public class TranslationThread extends ServiceThread {

	public TranslationThread(Socket clientSocket, Map<String, String> translationMap) throws IOException {
		super(clientSocket, translationMap);
	}

	@Override
	public void run() {
		try {
			String[] request = reader.readLine().trim().split("\\s+");
			String translation = (String) map.get(request[0].toLowerCase());

			Socket recipientSocket = null;
			BufferedWriter writer = null;
			try {
				recipientSocket = new Socket(request[1], Integer.parseInt(request[2]));
				writer = new BufferedWriter(
						new OutputStreamWriter(new BufferedOutputStream(recipientSocket.getOutputStream())));
				if (translation == null)
					translation = "No translation available";
				writer.write(translation + System.lineSeparator());
			} finally {
				if(writer != null)
					writer.close();
				if(recipientSocket != null)
					recipientSocket.close();
			}
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
