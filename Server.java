import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 4;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, Integer> scores = new HashMap<>();
    private static boolean gameInProgress = true;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다. 참여자자를 기다리는 중...");
            
            // 최대 4명 연결
            while (clients.size() < MAX_PLAYERS) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                client.start();
                System.out.println("새로운 참여자자 접속! 현재 인원: " + clients.size());
            }

            // 게임 시작
            while (gameInProgress) {
                System.out.println("모든 참여자자의 입력을 기다리는 중...");

                Map<String, String> choices = new HashMap<>();

                // 모든 참여자자의 입력을 기다림
                for (ClientHandler client : clients) {
                    String choice = client.getChoice();
                    if (choice != null) {
                        choices.put(client.getPlayerName(), choice);
                    }
                }

                //모든 참여자자가 입력했을 때만 진행
                if (choices.size() == clients.size()) {
                    broadcastResults(choices);

                    
                    for (ClientHandler client : clients) {
                        client.sendMessage("다음 라운드에 선택하세요.");
                        // 선택 초기화화
                        client.clearChoice(); 
                    }
                } else {
                    System.out.println("모든 참여자자가 입력하지 않았습니다. 다시 기다립니다.");
                    // 1초 대기 후 다시 체크
                    Thread.sleep(1000); 
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 결과 전송
    private static void broadcastResults(Map<String, String> choices) {
        System.out.println("결과 계산 중...");
        Map<String, String> results = new HashMap<>();

        // 모든 플레이어의 선택에 대해 결과 계산
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            String playerName = entry.getKey();
            String choice = entry.getValue();

            int wins = 0;
            for (String opponentChoice : choices.values()) {
                if (opponentChoice != null && !choice.equals(opponentChoice)) {
                    if (isWinner(choice, opponentChoice)) {
                        wins++;
                    }
                }
            }

            if (wins == choices.size() - 1) {
                results.put(playerName, "승리");
                scores.put(playerName, scores.getOrDefault(playerName, 0) + 1);
            } else {
                results.put(playerName, "패배 또는 무승부");
            }

            if (scores.getOrDefault(playerName, 0) >= 5) {
                gameInProgress = false;
                results.put(playerName, "최종 승리!");
            }
        }

        // 참여자에게 결과 전송
        for (ClientHandler client : clients) {
            client.sendResults(results);
        }
    }

    private static boolean isWinner(String choice, String opponentChoice) {
        return (choice.equals("가위") && opponentChoice.equals("보")) ||
               (choice.equals("바위") && opponentChoice.equals("가위")) ||
               (choice.equals("보") && opponentChoice.equals("바위"));
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private String choice;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            playerName = in.readLine();
            System.out.println(playerName + "이(가) 연결되었습니다.");

            while (true) {
                out.println("가위, 바위, 보 중 하나를 입력하세요:");
                choice = in.readLine();
                if (choice == null || choice.isEmpty()) {
                    choice = null; // 잘못된 입력 방지
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getChoice() {
        return choice;
    }

    public void clearChoice() {
        this.choice = null;
    }

    public void sendResults(Map<String, String> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 게임 결과 ===\n");
        for (Map.Entry<String, String> entry : results.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        out.println(sb.toString());
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}



