import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Point;

public class EraserTool extends MouseAdapter {

    private DrawingCanvas canvas;
    private int eraserWidth = 20;
    private Point2D cursorPoint = null;
    private Point lastErasePoint = null;

    private boolean isStrokeMode = false;

    public EraserTool(DrawingCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (canvas.isEraserMode()) {
            if (isStrokeMode) {
                eraseStroke(e.getX(), e.getY());
            } else {
                lastErasePoint = e.getPoint();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (canvas.isEraserMode()) {
            cursorPoint = e.getPoint();
            if (!isStrokeMode) {
                if (lastErasePoint != null) {
                    Graphics2D g2d = canvas.getBitmapLayerGraphics();
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(eraserWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(lastErasePoint.x, lastErasePoint.y, e.getX(), e.getY());
                    g2d.dispose();
                }
                lastErasePoint = e.getPoint();
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (canvas.isEraserMode() && !isStrokeMode) {
            lastErasePoint = null;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (canvas.isEraserMode()) {
            cursorPoint = e.getPoint();
            canvas.repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (canvas.isEraserMode()) {
            cursorPoint = null;
            canvas.repaint();
        }
    }

    public void draw(Graphics2D g2d) {
        if (cursorPoint != null && canvas.isEraserMode()) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval((int) (cursorPoint.getX() - eraserWidth / 2), (int) (cursorPoint.getY() - eraserWidth / 2), eraserWidth, eraserWidth);
            g2d.setComposite(originalComposite);
        }
    }

    private void eraseStroke(int x, int y) {
        ArrayList<ArrayList<DrawingObject>> strokes = canvas.getStrokes();
        Iterator<ArrayList<DrawingObject>> strokeIterator = strokes.iterator();
        Point2D mousePoint = new java.awt.geom.Point2D.Double(x, y);

        while (strokeIterator.hasNext()) {
            ArrayList<DrawingObject> stroke = strokeIterator.next();
            for (DrawingObject obj : stroke) {
                if (obj.intersectsPoint(mousePoint, eraserWidth)) {
                    strokeIterator.remove();
                    canvas.repaint();
                    return;
                }
            }
        }
    }

    public void setStrokeMode(boolean isStrokeMode) {
        this.isStrokeMode = isStrokeMode;
    }

    public boolean isStrokeMode() {
        return isStrokeMode;
    }

    public void clearAll() {
        canvas.getStrokes().clear();
        canvas.repaint();
    }

    public int getEraserWidth() {
        return eraserWidth;
    }

    public void setEraserWidth(int width) {
        this.eraserWidth = width;
    }
}