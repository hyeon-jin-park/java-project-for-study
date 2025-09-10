import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainApp {

    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("애니메이션 생성기");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // CardLayout을 사용할 패널
            cardPanel = new JPanel();
            cardLayout = new CardLayout();
            cardPanel.setLayout(cardLayout);

            // --- 홈 화면 패널 ---
            JPanel homePanel = createHomePanel();
            cardPanel.add(homePanel, "home");

            // --- 캔버스 화면 패널 ---
            JPanel canvasPanel = createDrawingPanel();
            cardPanel.add(canvasPanel, "canvas");


            // 프레임에 cardPanel 추가
            frame.add(cardPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 프로젝트 폴더를 정의합니다.
        File projectDir = new File("my_animations");
        if (!projectDir.exists()) {
            projectDir.mkdirs(); // 폴더가 없으면 새로 만듭니다.
        }

        // 파일 목록을 저장할 DefaultListModel을 생성합니다.
        DefaultListModel<String> fileListModel = new DefaultListModel<>();

        // 폴더 내의 파일 목록을 가져와서 JList에 추가합니다.
        File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".anim"));
        if (files != null) {
            for (File file : files) {
                fileListModel.addElement(file.getName().replace(".anim", ""));
            }
        }

        // JList에 모델을 연결합니다.
        JList<String> fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(fileList), BorderLayout.CENTER);

        // 버튼들을 담을 패널
        JPanel buttonPanel = new JPanel();

        JButton newButton = new JButton("새로 만들기");
        newButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "canvas");
        });

        JButton deleteButton = new JButton("삭제");
        JButton copyButton = new JButton("복사");

        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(copyButton);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.setPreferredSize(new Dimension(400, 300));

        return panel;
    }

    // MainApp 클래스 내부에 있는 메서드입니다.
    private static JPanel createDrawingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 상단 컨테이너 패널 (NORTH 영역에 들어갈 패널)
        JPanel topContainerPanel = new JPanel();
        topContainerPanel.setLayout(new BoxLayout(topContainerPanel, BoxLayout.Y_AXIS)); // 컴포넌트를 수직으로 쌓습니다.

        // 1. 메뉴바 생성
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 홈으로 메뉴
        JMenuItem homeMenuItem = new JMenuItem("홈으로");
        homeMenuItem.addActionListener(e -> cardLayout.show(cardPanel, "home"));

        // File 메뉴
        JMenu fileMenu = new JMenu("파일(File)");
        JMenuItem newMenuItem = new JMenuItem("새로 만들기");
        JMenuItem saveMenuItem = new JMenuItem("저장하기");
        JMenuItem openMenuItem = new JMenuItem("불러오기");
        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(openMenuItem);

        // Edit 메뉴
        JMenu editMenu = new JMenu("편집(Edit)");
        JMenuItem undoMenuItem = new JMenuItem("뒤로가기");
        JMenuItem redoMenuItem = new JMenuItem("다시실행");
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);

        // Help 메뉴
        JMenu helpMenu = new JMenu("도움말(Help)");

        menuBar.add(homeMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // 2. 상단 도구 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 핵심 도구 버튼
        JButton saveButton = new JButton("저장");
        JButton undoButton = new JButton("뒤로가기");
        JButton redoButton = new JButton("다시실행");
        JButton penButton = new JButton("펜");
        JButton eraserButton = new JButton("지우개");
        JButton ShapeButton = new JButton("도형");
        JButton selectButton = new JButton("선택");
        JButton colorButton = new JButton("색상 선택");


        topPanel.add(saveButton);
        topPanel.add(undoButton);
        topPanel.add(redoButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL)); // 구분선
        topPanel.add(penButton);
        topPanel.add(eraserButton);
        topPanel.add(ShapeButton);
        topPanel.add(selectButton);
        topPanel.add(colorButton);

        topContainerPanel.add(menuBar);
        topContainerPanel.add(topPanel);

        panel.add(topContainerPanel, BorderLayout.NORTH);

        // 3. 중앙 캔버스
        DrawingCanvas drawingCanvas = new DrawingCanvas();
        panel.add(drawingCanvas, BorderLayout.CENTER);

        // 오른쪽 패널 (두 개의 설정 패널을 수직으로 배치)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // 펜 설정 패널
        JPanel penSettingsPanel = new ToolSettingsPanel(drawingCanvas, false);
        rightPanel.add(penSettingsPanel);

        // 지우개 설정 패널
        JPanel eraserSettingsPanel = new ToolSettingsPanel(drawingCanvas, true);
        rightPanel.add(eraserSettingsPanel);

        panel.add(rightPanel, BorderLayout.EAST);

        // 4. 하단 프레임 패널
        FramePanel framePanel = new FramePanel();
        panel.add(framePanel, BorderLayout.SOUTH);


        // 5. 추가 기능들

        // 색상 변경
        colorButton.addActionListener(e -> {
            // JColorChooser를 사용해 색상 선택 다이얼로그 띄우기
            Color newColor = JColorChooser.showDialog(panel, "색상 선택", drawingCanvas.getCurrentColor());
            if (newColor != null) {
                // 사용자가 색상을 선택했다면 캔버스에 적용
                drawingCanvas.setCurrentColor(newColor);
            }
        });

        // 지우개 전환
        penButton.addActionListener(e -> {
            drawingCanvas.setEraserMode(false); // 펜 버튼 클릭 시 지우개 모드 끄기
        });

        // 지우개 버튼 리스너
        eraserButton.addActionListener(e -> {
            drawingCanvas.setEraserMode(true); // 지우개 버튼 클릭 시 지우개 모드 켜기
        });


        return panel;
    }
}