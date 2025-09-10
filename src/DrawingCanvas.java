import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DrawingCanvas extends JPanel {

    public enum ToolMode {
        PEN, ERASER, SELECT
    }

    private ArrayList<ArrayList<DrawingObject>> undoStack = new ArrayList<>();
    private ArrayList<ArrayList<DrawingObject>> redoStack = new ArrayList<>();
    private ArrayList<DrawingObject> currentStroke = null;

    private Point lastPoint = null;
    private Color penColor = Color.BLACK;
    private int penStrokeWidth = 5;
    private int penOpacity = 255;
    private int penStrokeCap = BasicStroke.CAP_ROUND;
    private int penStrokeJoin = BasicStroke.JOIN_ROUND;

    private SelectionTool selectionTool;
    private EraserTool eraserTool;
    private ToolMode currentMode = ToolMode.PEN;

    private BufferedImage offScreenBuffer;
    private BufferedImage bitmapLayer;

    public DrawingCanvas() {
        setBackground(Color.WHITE);

        selectionTool = new SelectionTool(this);
        eraserTool = new EraserTool(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentMode == ToolMode.SELECT) {
                    selectionTool.mousePressed(e);
                } else if (currentMode == ToolMode.ERASER) {
                    eraserTool.mousePressed(e);
                } else if (currentMode == ToolMode.PEN) {
                    currentStroke = new ArrayList<>();
                    lastPoint = e.getPoint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentMode == ToolMode.SELECT) {
                    selectionTool.mouseReleased(e);
                } else if (currentMode == ToolMode.ERASER) {
                    // EraserTool이 mousePressed에서 지우므로, 여기서는 할 일이 없습니다.
                } else if (currentMode == ToolMode.PEN) {
                    if (currentStroke != null && !currentStroke.isEmpty()) {
                        undoStack.add(currentStroke);
                        redoStack.clear();
                    }
                    currentStroke = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentMode == ToolMode.SELECT) {
                    selectionTool.mouseDragged(e);
                } else if (currentMode == ToolMode.ERASER) {
                    eraserTool.mouseDragged(e);
                } else if (currentMode == ToolMode.PEN && lastPoint != null) {
                    Point currentPoint = e.getPoint();
                    currentStroke.add(new Line(lastPoint, currentPoint, penColor, penStrokeWidth, penStrokeCap, penStrokeJoin));
                    lastPoint = currentPoint;
                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                eraserTool.mouseMoved(e);
            }
        });

        addMouseListener(eraserTool);
        addMouseMotionListener(eraserTool);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Dimension size = getPreferredSize();
        offScreenBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        bitmapLayer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bitmapLayer.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size.width, size.height);
        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Graphics2D bufferGraphics = offScreenBuffer.createGraphics();
        bufferGraphics.setColor(Color.WHITE);
        bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

        bufferGraphics.drawImage(bitmapLayer, 0, 0, null);

        bufferGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (ArrayList<DrawingObject> stroke : undoStack) {
            for (DrawingObject obj : stroke) {
                obj.draw(bufferGraphics);
            }
        }
        if (currentStroke != null) {
            for (DrawingObject obj : currentStroke) {
                obj.draw(bufferGraphics);
            }
        }
        bufferGraphics.dispose();

        g2d.drawImage(offScreenBuffer, 0, 0, this);

        selectionTool.draw(g2d);
        eraserTool.draw(g2d);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ArrayList<DrawingObject> lastStroke = undoStack.remove(undoStack.size() - 1);
            redoStack.add(lastStroke);
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            ArrayList<DrawingObject> lastUndone = redoStack.remove(redoStack.size() - 1);
            undoStack.add(lastUndone);
            repaint();
        }
    }

    public void setToolMode(ToolMode mode) {
        this.currentMode = mode;
        repaint();
    }

    public ArrayList<ArrayList<DrawingObject>> getStrokes() {
        return undoStack;
    }

    public boolean isSelectMode() { return currentMode == ToolMode.SELECT; }
    public boolean isEraserMode() { return currentMode == ToolMode.ERASER; }

    public Color getPenColor() { return penColor; }
    public void setPenColor(Color color) { this.penColor = color; }
    public int getPenStrokeWidth() { return penStrokeWidth; }
    public void setPenStrokeWidth(int width) { this.penStrokeWidth = width; }
    public int getPenOpacity() { return penOpacity; }
    public void setPenOpacity(int opacity) { this.penOpacity = opacity; }
    public void setPenStrokeShape(int shapeCap, int shapeJoin) {
        this.penStrokeCap = shapeCap;
        this.penStrokeJoin = shapeJoin;
    }

    public EraserTool getEraserTool() { return eraserTool; }
    public SelectionTool getSelectionTool() { return selectionTool; }
    public void setEraserWidth(int width) { eraserTool.setEraserWidth(width); }
    public Graphics2D getBitmapLayerGraphics() { return bitmapLayer.createGraphics(); }
}