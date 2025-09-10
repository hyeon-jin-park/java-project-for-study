import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FramePanel extends JPanel {

    private JList<ImageIcon> frameList;
    private DefaultListModel<ImageIcon> listModel;
    private ArrayList<BufferedImage> frames;

    public FramePanel() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("프레임"));

        listModel = new DefaultListModel<>();
        frameList = new JList<>(listModel);
        frameList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        frameList.setVisibleRowCount(1); // 한 줄로 표시

        // 프레임 미리보기를 스크롤 가능하게 만듭니다.
        JScrollPane scrollPane = new JScrollPane(frameList);
        this.add(scrollPane, BorderLayout.CENTER);

        // 프레임 추가, 삭제 버튼
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("프레임 추가");
        JButton deleteButton = new JButton("프레임 삭제");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // 테스트용으로 빈 프레임 하나 추가
        addFrame(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));

        // TODO: 버튼 리스너 추가 로직
        addButton.addActionListener(e -> {
            // 새로운 프레임을 추가하는 로직
            addFrame(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
        });

        deleteButton.addActionListener(e -> {
            // 선택된 프레임을 삭제하는 로직
            int selectedIndex = frameList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
                frames.remove(selectedIndex);
            }
        });
    }

    public void addFrame(BufferedImage frameImage) {
        if (frames == null) {
            frames = new ArrayList<>();
        }
        frames.add(frameImage);

        // 프레임 이미지의 썸네일(작은 미리보기)을 생성하여 JList에 추가
        ImageIcon thumbnail = new ImageIcon(frameImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        listModel.addElement(thumbnail);
    }

    // 다른 클래스에서 프레임 목록에 접근할 수 있도록 하는 메서드
    public ArrayList<BufferedImage> getFrames() {
        return frames;
    }
}