import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, Integer> scoreMap = new HashMap<>();
    private static final int WIN_COUNT = 5;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String playerName = in.readLine();
                scoreMap.putIfAbsent(playerName, 0);
                while (true) {
                    String clientInput = in.readLine();
                    if (clientInput == null) break;

                    String serverChoice = getRandomChoice();
                    String result = determineWinner(clientInput, serverChoice);

                    if (result.equals("WIN")) {
                        scoreMap.put(playerName, scoreMap.get(playerName) + 1);
                    }

                    if (scoreMap.get(playerName) >= WIN_COUNT) {
                        out.println("승리: " + playerName);
                        break;
                    } else {
                        out.println("결과: " + result + ", 서버 선택: " + serverChoice);
                    }
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getRandomChoice() {
            String[] choices = {"가위", "바위", "보"};
            return choices[new Random().nextInt(3)];
        }

        private String determineWinner(String clientChoice, String serverChoice) {
            if (clientChoice.equals(serverChoice)) return "무승부";
            if ((clientChoice.equals("가위") && serverChoice.equals("보")) ||
                (clientChoice.equals("바위") && serverChoice.equals("가위")) ||
                (clientChoice.equals("보") && serverChoice.equals("바위"))) {
                return "WIN";
            }
            return "LOSE";
        }
    }
}
