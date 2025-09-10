import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeListener;

public class ToolSettingsPanel extends JPanel {

    private DrawingCanvas canvas;
    private boolean isEraser;

    public ToolSettingsPanel(DrawingCanvas canvas, boolean isEraser) {
        this.canvas = canvas;
        this.isEraser = isEraser;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        String title = isEraser ? "지우개 설정" : "펜 설정";
        this.setBorder(BorderFactory.createTitledBorder(title));

        // 모양 설정
        JPanel shapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shapePanel.setBorder(BorderFactory.createTitledBorder("모양"));
        JRadioButton roundButton = new JRadioButton("원형", true);
        JRadioButton squareButton = new JRadioButton("사각형");
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(roundButton);
        shapeGroup.add(squareButton);
        shapePanel.add(roundButton);
        shapePanel.add(squareButton);

        // 굵기 슬라이더
        JLabel thicknessLabel = new JLabel("굵기");
        JSlider thicknessSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, isEraser ? 20 : 5);
        thicknessSlider.setMajorTickSpacing(10);
        thicknessSlider.setMinorTickSpacing(1);
        thicknessSlider.setPaintTicks(true);
        thicknessSlider.setPaintLabels(true);

        // 투명도 슬라이더
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

        // 리스너 연결
        ChangeListener sliderListener = e -> {
            if (isEraser) {
                canvas.setEraserStrokeWidth(thicknessSlider.getValue());
                canvas.setEraserOpacity(opacitySlider.getValue());
            } else {
                canvas.setPenStrokeWidth(thicknessSlider.getValue());
                canvas.setPenOpacity(opacitySlider.getValue());
            }
        };
        thicknessSlider.addChangeListener(sliderListener);
        opacitySlider.addChangeListener(sliderListener);

        roundButton.addActionListener(e -> {
            if (isEraser) { canvas.setEraserStrokeShape(BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); }
            else { canvas.setPenStrokeShape(BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); }
        });

        squareButton.addActionListener(e -> {
            if (isEraser) { canvas.setEraserStrokeShape(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER); }
            else { canvas.setPenStrokeShape(BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER); }
        });
    }
}