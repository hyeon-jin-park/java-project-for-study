import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line implements DrawingObject {

    private Point startPoint;
    private Point endPoint;
    private Color color;
    private int strokeWidth;
    private int strokeCap;
    private int strokeJoin;
    private boolean isSelected = false;

    public Line(Point startPoint, Point endPoint, Color color, int strokeWidth, int strokeCap, int strokeJoin) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokeCap = strokeCap;
        this.strokeJoin = strokeJoin;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isSelected) {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(strokeWidth + 3, strokeCap, strokeJoin));
            g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth, strokeCap, strokeJoin));
        g2d.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }

    @Override
    public boolean intersects(Rectangle rect) {
        Line2D line = new Line2D.Double(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        return line.intersects(rect);
    }

    @Override
    public boolean intersectsPoint(Point2D point, int tolerance) {
        Line2D line = new Line2D.Double(startPoint, endPoint);
        double distance = line.ptSegDist(point);
        return distance < tolerance;
    }

    @Override
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public Point getStartPoint() {
        return startPoint;
    }

    @Override
    public Point getEndPoint() {
        return endPoint;
    }

}