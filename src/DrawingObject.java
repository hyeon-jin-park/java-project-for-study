import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.geom.Point2D;

public interface DrawingObject {
    void draw(Graphics2D g2d);
    void setSelected(boolean selected);
    boolean isSelected();
    boolean intersects(Rectangle rect);
    boolean intersectsPoint(Point2D point, int tolerance);
    Point getStartPoint();
    Point getEndPoint();
}