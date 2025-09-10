import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ToolSettingsPanel extends JPanel {

    private DrawingCanvas canvas;
    private boolean isEraser;

    public ToolSettingsPanel(DrawingCanvas canvas, boolean isEraser) {
        this.canvas = canvas;
        this.isEraser = isEraser;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        String title = isEraser ? "지우개 설정" : "펜 설정";
        this.setBorder(BorderFactory.createTitledBorder(title));

        if (isEraser) {
            setupEraserPanel();
        } else {
            setupPenPanel();
        }
    }

    private void setupPenPanel() {
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shapePanel.setBorder(BorderFactory.createTitledBorder("모양"));
        JRadioButton roundButton = new JRadioButton("원형", true);
        JRadioButton squareButton = new JRadioButton("사각형");
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(roundButton);
        shapeGroup.add(squareButton);
        shapePanel.add(roundButton);
        shapePanel.add(squareButton);

        JLabel thicknessLabel = new JLabel("굵기");
        JSlider thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, 5);
        thicknessSlider.setMajorTickSpacing(10);
        thicknessSlider.setMinorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);

        JLabel opacityLabel = new JLabel("투명도");
        JSlider opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
        opacitySlider.setMajorTickSpacing(50);
        opacitySlider.setMinorTickSpacing(10);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);

        this.add(shapePanel);
        this.add(thicknessLabel);
        this.add(thicknessSlider);
        this.add(opacityLabel);
        this.add(opacitySlider);

        thicknessSlider.addChangeListener(e -> canvas.setPenStrokeWidth(thicknessSlider.getValue()));
        opacitySlider.addChangeListener(e -> canvas.setPenOpacity(opacitySlider.getValue()));

        roundButton.addActionListener(e -> canvas.setPenStrokeShape(BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        squareButton.addActionListener(e -> canvas.setPenStrokeShape(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
    }

    private void setupEraserPanel() {
        JLabel thicknessLabel = new JLabel("굵기");
        JSlider thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, 20);
        thicknessSlider.setMajorTickSpacing(10);
        thicknessSlider.setMinorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);

        JButton eraseModeButton = new JButton("획 지우기");
        JButton clearAllButton = new JButton("전체 지우기");

        this.add(thicknessLabel);
        this.add(thicknessSlider);
        this.add(eraseModeButton);
        this.add(clearAllButton);

        thicknessSlider.addChangeListener(e -> canvas.getEraserTool().setEraserWidth(thicknessSlider.getValue()));
        eraseModeButton.addActionListener(e -> {
            boolean isStrokeMode = canvas.getEraserTool().isStrokeMode();
            if (isStrokeMode) {
                // 획 지우기 모드 -> 연속 지우기 모드로 전환
                canvas.getEraserTool().setStrokeMode(false);
                eraseModeButton.setText("연속 지우기");
            } else {
                // 연속 지우기 모드 -> 획 지우기 모드로 전환
                canvas.getEraserTool().setStrokeMode(true);
                eraseModeButton.setText("획 지우기");
            }
        });
        clearAllButton.addActionListener(e -> canvas.getEraserTool().clearAll());
    }
}