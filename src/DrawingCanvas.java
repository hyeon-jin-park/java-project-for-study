import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DrawingCanvas extends JPanel {

    // 캔버스 변수
    private BufferedImage canvasImage;
    private Graphics2D g2d;
    private int prevX, prevY;

    // 색상 변수
    private Color currentColor = Color.BLACK; // 펜의 기본 색상
    private boolean isEraserMode = false; // 지우개 모드 상태

    // 도구 변수
    private int penStrokeWidth = 5;
    private int penOpacity = 255;
    private int penStrokeCap = BasicStroke.CAP_ROUND;
    private int penStrokeJoin = BasicStroke.JOIN_ROUND;

    private int eraserStrokeWidth = 5;
    private int eraserOpacity = 255;
    private int eraserStrokeCap = BasicStroke.CAP_ROUND;
    private int eraserStrokeJoin = BasicStroke.JOIN_ROUND;

    public DrawingCanvas() {
        canvasImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2d = canvasImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 800, 600);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 현재 도구 상태에 따라 그리기 색상 설정
                if (isEraserMode) {
                    g2d.setColor(Color.WHITE); // 지우개 모드일 땐 흰색으로
                } else {
                    g2d.setColor(currentColor); // 펜 모드일 땐 현재 펜 색상으로
                }

                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(prevX, prevY, e.getX(), e.getY());

                prevX = e.getX();
                prevY = e.getY();

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    // 외부에서 펜 색상을 변경하는 메서드
    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    // 외부에서 지우개 모드를 설정하는 메서드
    public void setEraserMode(boolean isEraserMode) {
        this.isEraserMode = isEraserMode;
    }

    // 캔버스 이미지를 반환하는 메서드 (저장 기능에 사용)
    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    // 펜 설정을 위한 함수들
    public void setPenColor(Color color) {
        this.currentColor = color;
    }

    public void setPenStrokeWidth(int width) {
        this.penStrokeWidth = width;
    }

    public void setPenOpacity(int opacity) {
        this.penOpacity = opacity;
    }

    public void setPenStrokeShape(int shapeCap, int shapeJoin) {
        this.penStrokeCap = shapeCap;
        this.penStrokeJoin = shapeJoin;
    }

    // 지우개 설정을 위한 함수들
    public void setEraserStrokeWidth(int width) {
        this.eraserStrokeWidth = width;
    }

    public void setEraserOpacity(int opacity) {
        this.eraserOpacity = opacity;
    }

    public void setEraserStrokeShape(int shapeCap, int shapeJoin) {
        this.eraserStrokeCap = shapeCap;
        this.eraserStrokeJoin = shapeJoin;
    }

    // 도구 모드 설정 함수
    public void setToolMode(boolean isEraser) {
        this.isEraserMode = isEraser;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("그림판");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DrawingCanvas canvas = new DrawingCanvas();
            frame.add(canvas);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}