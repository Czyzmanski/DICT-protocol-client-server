package zad1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application {
	private GridPane gridPane = new GridPane();
	private Label wordLabel = new Label("Word to translate: ");
	private Label languageLabel = new Label("Language to translate: ");
	private Label portLabel = new Label("Port number: ");
	private TextField wordField = new TextField();
	private TextField languageField = new TextField();
	private TextField portField = new TextField();
	private Button okButton = new Button("OK");
	private HBox hBox = new HBox(10);
	private Text responseText = new Text();
	private Scene scene = new Scene(gridPane, 640, 480);

	@Override
	public void start(Stage primaryStage) throws Exception {
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(25, 25, 25, 25));

		gridPane.add(wordLabel, 0, 0);
		gridPane.add(languageLabel, 0, 1);
		gridPane.add(portLabel, 0, 2);
		gridPane.add(wordField, 1, 0);
		gridPane.add(languageField, 1, 1);
		gridPane.add(portField, 1, 2);

		okButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					connect();
				} catch (NumberFormatException e1) {
					Alert error = new Alert(AlertType.ERROR, "Invalid port number format.");
					error.showAndWait();
				} catch (IOException e1) {
					Alert error = new Alert(AlertType.ERROR, "Connection to server failed. Please try again.");
					error.showAndWait();
					e1.printStackTrace();
				}
			}
		});

		hBox.setAlignment(Pos.BOTTOM_RIGHT);
		hBox.getChildren().add(okButton);

		gridPane.add(hBox, 1, 3);
		gridPane.add(responseText, 0, 5, 2, 1);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Dict client");
		primaryStage.show();
	}

	private void connect() throws NumberFormatException, IOException {
		String word = wordField.getText().trim().toLowerCase();
		String language = languageField.getText().trim().toUpperCase();
		int portNumber = Integer.parseInt(portField.getText().trim());

		Socket requestSocket = null;
		BufferedWriter requestWriter = null;

		ServerSocket listeningSocket = null;

		Socket responseSocket = null;
		BufferedReader responseReader = null;
		
		try {
			requestSocket = new Socket("localhost", 9004);
			requestWriter = new BufferedWriter(
					new OutputStreamWriter(new BufferedOutputStream(requestSocket.getOutputStream())));

			listeningSocket = new ServerSocket(portNumber);

			requestWriter.write(String.format("%s %s %d%n", word, language, portNumber));
			requestWriter.flush();

			responseSocket = listeningSocket.accept();
			responseReader = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(responseSocket.getInputStream())));

			String response = responseReader.readLine().trim();
			responseText.setText("RESPONSE: " + response);
		} finally {
			if (requestWriter != null)
				requestWriter.close();
			if (requestSocket != null)
				requestSocket.close();
			if (responseReader != null)
				responseReader.close();
			if (responseSocket != null)
				responseSocket.close();
			if(listeningSocket != null)
				listeningSocket.close();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
