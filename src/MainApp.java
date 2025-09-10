import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainApp {

    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("애니메이션 생성기");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            cardPanel = new JPanel();
            cardLayout = new CardLayout();
            cardPanel.setLayout(cardLayout);

            JPanel homePanel = createHomePanel();
            cardPanel.add(homePanel, "home");

            JPanel canvasPanel = createDrawingPanel();
            cardPanel.add(canvasPanel, "canvas");

            JMenuBar menuBar = createMenuBar(canvasPanel);
            frame.setJMenuBar(menuBar);

            frame.add(cardPanel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static JMenuBar createMenuBar(JPanel canvasPanel) {
        JMenuBar menuBar = new JMenuBar();
        DrawingCanvas drawingCanvas = findDrawingCanvas(canvasPanel);

        JMenuItem homeMenuItem = new JMenuItem("홈으로");
        homeMenuItem.addActionListener(e -> cardLayout.show(cardPanel, "home"));

        JMenu fileMenu = new JMenu("파일(File)");
        JMenuItem newMenuItem = new JMenuItem("새로 만들기");
        JMenuItem saveMenuItem = new JMenuItem("저장하기");
        JMenuItem openMenuItem = new JMenuItem("불러오기");

        fileMenu.add(homeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(openMenuItem);

        JMenu editMenu = new JMenu("편집(Edit)");
        JMenuItem undoMenuItem = new JMenuItem("뒤로가기");
        JMenuItem redoMenuItem = new JMenuItem("다시 실행");
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);

        JMenu helpMenu = new JMenu("도움말(Help)");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // Undo/Redo 메뉴 항목에 리스너 연결
        undoMenuItem.addActionListener(e -> drawingCanvas.undo());
        redoMenuItem.addActionListener(e -> drawingCanvas.redo());

        return menuBar;
    }

    private static JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        File projectDir = new File("my_animations");
        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        File[] files = projectDir.listFiles((dir, name) -> name.endsWith(".anim"));
        if (files != null) {
            for (File file : files) {
                fileListModel.addElement(file.getName().replace(".anim", ""));
            }
        }

        JList<String> fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(fileList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton newButton = new JButton("새로 만들기");
        newButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "canvas");
        });

        JButton deleteButton = new JButton("삭제");
        JButton copyButton = new JButton("복사");

        buttonPanel.add(newButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(copyButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(400, 300));

        return panel;
    }

    private static JPanel createDrawingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DrawingCanvas drawingCanvas = new DrawingCanvas();

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createTitledBorder("도구"));
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton undoButton = new JButton("뒤로가기");
        JButton redoButton = new JButton("다시 실행");
        JButton penButton = new JButton("펜");
        JButton eraserButton = new JButton("지우개");
        JButton selectButton = new JButton("선택");
        JButton colorButton = new JButton("색상 선택");

        topPanel.add(undoButton);
        topPanel.add(redoButton);
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(penButton);
        topPanel.add(eraserButton);
        topPanel.add(selectButton);
        topPanel.add(colorButton);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(drawingCanvas, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel penSettingsPanel = new ToolSettingsPanel(drawingCanvas, false);
        rightPanel.add(penSettingsPanel);

        JPanel eraserSettingsPanel = new ToolSettingsPanel(drawingCanvas, true);
        rightPanel.add(eraserSettingsPanel);

        panel.add(rightPanel, BorderLayout.EAST);

        FrameManagerPanel frameManagerPanel = new FrameManagerPanel();
        panel.add(frameManagerPanel, BorderLayout.SOUTH);

        // 버튼 리스너 연결
        undoButton.addActionListener(e -> drawingCanvas.undo());
        redoButton.addActionListener(e -> drawingCanvas.redo());
        penButton.addActionListener(e -> drawingCanvas.setToolMode(DrawingCanvas.ToolMode.PEN));
        eraserButton.addActionListener(e -> drawingCanvas.setToolMode(DrawingCanvas.ToolMode.ERASER));
        selectButton.addActionListener(e -> drawingCanvas.setToolMode(DrawingCanvas.ToolMode.SELECT));

        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(panel, "색상 선택", drawingCanvas.getPenColor());
            if (newColor != null) {
                drawingCanvas.setPenColor(newColor);
            }
        });

        return panel;
    }

    // canvasPanel에서 DrawingCanvas를 찾는 헬퍼 메서드
    private static DrawingCanvas findDrawingCanvas(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof DrawingCanvas) {
                return (DrawingCanvas) comp;
            } else if (comp instanceof JPanel) {
                DrawingCanvas canvas = findDrawingCanvas((JPanel) comp);
                if (canvas != null) {
                    return canvas;
                }
            }
        }
        return null;
    }
}