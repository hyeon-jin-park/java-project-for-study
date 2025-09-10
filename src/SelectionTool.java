import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.AlphaComposite;
import java.util.ArrayList;

public class SelectionTool extends MouseAdapter {

    private DrawingCanvas canvas;
    private Point selectStartPoint = null;
    private Point selectCurrentPoint = null;
    private boolean isSelecting = false;
    private boolean isLassoMode = false;
    private ArrayList<Point> lassoPoints = new ArrayList<>();

    public SelectionTool(DrawingCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (canvas.isSelectMode()) {
            // 모든 객체 선택 해제
            for (ArrayList<DrawingObject> stroke : canvas.getStrokes()) {
                stroke.forEach(obj -> obj.setSelected(false));
            }
            selectStartPoint = e.getPoint();
            selectCurrentPoint = e.getPoint();
            isSelecting = true;
            if (isLassoMode) {
                lassoPoints.clear();
                lassoPoints.add(e.getPoint());
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isSelecting) {
            if (isLassoMode) {
                selectObjectsInLasso();
            } else {
                selectObjectsInRectangle();
            }
            selectStartPoint = null;
            selectCurrentPoint = null;
            isSelecting = false;
            canvas.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isSelecting) {
            selectCurrentPoint = e.getPoint();
            if (isLassoMode) {
                lassoPoints.add(e.getPoint());
            }
            canvas.repaint();
        }
    }

    public void draw(Graphics2D g2d) {
        if (isSelecting && selectStartPoint != null && selectCurrentPoint != null) {
            if (isLassoMode) {
                // 올가미 선택 영역 그리기
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));
                if (lassoPoints.size() > 1) {
                    for (int i = 0; i < lassoPoints.size() - 1; i++) {
                        g2d.drawLine(lassoPoints.get(i).x, lassoPoints.get(i).y, lassoPoints.get(i+1).x, lassoPoints.get(i+1).y);
                    }
                }
            } else {
                // 사각형 선택 영역 그리기
                int x = Math.min(selectStartPoint.x, selectCurrentPoint.x);
                int y = Math.min(selectStartPoint.y, selectCurrentPoint.y);
                int width = Math.abs(selectStartPoint.x - selectCurrentPoint.x);
                int height = Math.abs(selectStartPoint.y - selectCurrentPoint.y);

                g2d.setColor(new Color(0, 0, 255, 50));
                g2d.fillRect(x, y, width, height);

                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));
                g2d.drawRect(x, y, width, height);
            }
        }
    }

    private void selectObjectsInRectangle() {
        if (selectStartPoint == null || selectCurrentPoint == null) {
            return;
        }

        Rectangle selectionRect = new Rectangle(
                Math.min(selectStartPoint.x, selectCurrentPoint.x),
                Math.min(selectStartPoint.y, selectCurrentPoint.y),
                Math.abs(selectStartPoint.x - selectCurrentPoint.x),
                Math.abs(selectStartPoint.y - selectCurrentPoint.y)
        );

        for (ArrayList<DrawingObject> stroke : canvas.getStrokes()) {
            boolean isSelected = false;
            for (DrawingObject obj : stroke) {
                if (obj.intersects(selectionRect)) {
                    isSelected = true;
                    break;
                }
            }
            // 획의 한 부분이 선택 영역에 포함되면 획 전체를 선택
            if (isSelected) {
                stroke.forEach(obj -> obj.setSelected(true));
            } else {
                stroke.forEach(obj -> obj.setSelected(false));
            }
        }
    }

    private void selectObjectsInLasso() {
        if (lassoPoints.size() < 3) {
            return; // 올가미가 다각형을 형성하려면 최소 3개의 점이 필요합니다.
        }

        Polygon lassoPolygon = new Polygon();
        for(Point p : lassoPoints) {
            lassoPolygon.addPoint(p.x, p.y);
        }

        for(ArrayList<DrawingObject> stroke : canvas.getStrokes()) {
            boolean isSelected = false;
            for(DrawingObject obj : stroke) {
                if (lassoPolygon.contains(obj.getStartPoint()) || lassoPolygon.contains(obj.getEndPoint())) {
                    isSelected = true;
                    break;
                }
            }
            if (isSelected) {
                stroke.forEach(obj -> obj.setSelected(true));
            } else {
                stroke.forEach(obj -> obj.setSelected(false));
            }
        }
    }

    public void setLassoMode(boolean isLasso) {
        this.isLassoMode = isLasso;
    }
}