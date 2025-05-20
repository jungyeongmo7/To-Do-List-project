import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class Client extends Application {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    @Override
    public void start(Stage primaryStage) {
        TextArea outputArea = new TextArea();
        TextField inputField = new TextField();
        Button sendButton = new Button("전송");

        VBox root = new VBox(10, outputArea, inputField, sendButton);
        Scene scene = new Scene(root, 300, 300);
        primaryStage.setTitle("가위바위보 클라이언트");
        primaryStage.setScene(scene);
        primaryStage.show();

        sendButton.setOnAction(event -> {
            String playerName = "Player1";
            String input = inputField.getText();

            try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(playerName);
                out.println(input);
                String response = in.readLine();
                outputArea.appendText(response + "\n");

            } catch (IOException e) {
                outputArea.appendText("연결 오류: " + e.getMessage() + "\n");
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
