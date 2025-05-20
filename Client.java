import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class Client extends Application {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    private BufferedReader in;
    private PrintWriter out;
    private TextArea outputArea;
    private String playerName;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // 사용자 이름 입력 받기
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("플레이어 이름 입력");
        nameDialog.setHeaderText("플레이어 이름을 입력하세요:");
        nameDialog.setContentText("이름:");
        playerName = nameDialog.showAndWait().orElse("Player");

        // UI 구성
        outputArea = new TextArea();
        outputArea.setEditable(false);
        TextField inputField = new TextField();
        Button sendButton = new Button("전송");

        VBox root = new VBox(10, outputArea, inputField, sendButton);
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("가위바위보 클라이언트");
        primaryStage.setScene(scene);
        primaryStage.show();

        // 서버 연결 시도
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 플레이어 이름 전송
            out.println(playerName);

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        outputArea.appendText(message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            outputArea.appendText("서버 연결에 실패하였습니다.\n");
        }

        // 버튼 클릭 시 가위바위보 전송
        sendButton.setOnAction(event -> {
            String choice = inputField.getText().trim();
            if (choice.equals("가위") || choice.equals("바위") || choice.equals("보")) {
                out.println(choice);
                inputField.clear();
            } else {
                outputArea.appendText("올바른 입력이 아닙니다. (가위, 바위, 보 중 하나만 입력)\n");
            }
        });
    }
}
