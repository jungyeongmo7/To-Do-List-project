//GUI를 만들기 위한 도구를 부르는 코드
import javax.swing.*; //버튼,입력창
import java.awt.*; //레이아웃이나 크기조절(그래픽적인 기능)

public class Try1 extends JFrame { //클래스 (화면창)
//화면에 보일것들 선언
private DefaultListModel<String> listModel; //리스트에 들어갈 할 일 목록을 저장할 공간
private JList<String> todoList; //할 일 목록을 화면에 보여주는 리스트
private JTextField inputField; //할 일을 입력하는 텍스트 칸
private JButton addButton, editButton, deleteButton; //각각 "추가", "수정", "삭제" 버튼
//화면 창을 만들고 꾸미는 역할,Try1을 실행하면 여기 코드가 작동
public Try1() {
	//창의 제목을 "To-Do List"로
    setTitle("To-Do List");
    //창의 크기를 가로 400, 세로 300 픽셀로
    setSize(400, 300);
    //창을 닫으면 프로그램이 완전히 종료되게 만드는 코드(안 쓰면 창만 닫히고 백그라운드에 남음)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //창 안의 구조를 정하는 부분, BorderLayout은 화면을 5개 구역(북/남/동/서/중앙)으로 나눌 수 있게 해줌
    setLayout(new BorderLayout());

    // 입력 필드와 버튼들, 각 요소들을 실제로
    inputField = new JTextField(15); //가로 길이 15짜리 입력칸
    addButton = new JButton("추가");
    editButton = new JButton("수정");
    deleteButton = new JButton("삭제"); //각 버튼에는 "추가", "수정", "삭제"라는 이름이 붙음
    deleteButton.setPreferredSize(new Dimension(70, 25)); // ✅ 버튼 크기 지정, 삭제 버튼 크기를 약간 조절해서 더 잘 보이게

    //버튼과 입력창을 가로로 나열할 수 있는 패널(작은 영역), FlowLayout은 왼쪽 → 오른쪽으로 자연스럽게 배치되는 방식
    JPanel inputPanel = new JPanel(new FlowLayout());
    //위에서 만든 입력칸과 버튼들을 inputPanel에, 순서대로 입력칸 → 추가 → 수정 → 삭제가 화면 상단에
    inputPanel.add(inputField);
    inputPanel.add(addButton);
    inputPanel.add(editButton);
    inputPanel.add(deleteButton);
    //위에서 만든 inputPanel을 화면 상단(NORTH)에 배치
    add(inputPanel, BorderLayout.NORTH);

    // 할 일을 화면에 보이게 하는 리스트
    listModel = new DefaultListModel<>(); //할 일을 담는 데이터 공간
    todoList = new JList<>(listModel); //실제로 보여주는 리스트
    todoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //한 번에 하나만 선택할 수 있게 <-여러ㅓ개 선택도 괜찮을지도..
    //추가 기능, todoList를 스크롤 가능한 형태로 만들어서, 화면 중앙(CENTER)에 배치(할 일이 많아지면 스크롤해서 볼 수 있음)
    add(new JScrollPane(todoList), BorderLayout.CENTER);

    // "추가" 버튼을 눌렀을 때->입력칸에 텍스트가 있으면 리스트에 추가하고, 입력칸을 비움
    addButton.addActionListener(e -> {
        String task = inputField.getText().trim();
        if (!task.isEmpty()) {
            listModel.addElement(task);
            inputField.setText("");
        }
    });

    // 수정 기능 "수정" 버튼을 눌렀을 때->리스트에서 선택된 항목이 있다면, 입력칸의 내용으로 수정
    editButton.addActionListener(e -> {
        int selectedIndex = todoList.getSelectedIndex();
        String newTask = inputField.getText().trim();
        if (selectedIndex != -1 && !newTask.isEmpty()) {
            listModel.set(selectedIndex, newTask);
            inputField.setText("");
        }
    });

    // 삭제 기능 "삭제"버튼 -> 선택된 항목이 있다면 삭제
    deleteButton.addActionListener(e -> {
        int selectedIndex = todoList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        }
    });
   //모든 준비가 끝났으니 화면을 보이게 해주는 코드
    setVisible(true);
}
//Try1이라는 창을 띄우는 코드
public static void main(String[] args) {
SwingUtilities.invokeLater(Try1::new);
}
}
