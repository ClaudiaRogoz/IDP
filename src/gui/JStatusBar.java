package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
 
import javax.swing.JComponent;
import javax.swing.JPanel;
 
public class JStatusBar extends JPanel {
 
    private static final long serialVersionUID = 1L;
 
    protected JPanel leftPanel;
    protected JPanel rightPanel;
 
    public JStatusBar() {
        createPartControl();
    }
 
    protected void createPartControl() {    
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(getWidth(), 23));
 
        leftPanel = new JPanel(new FlowLayout(
                FlowLayout.LEADING, 5, 3));
        leftPanel.setOpaque(false);
        add(leftPanel, BorderLayout.WEST);
 
        rightPanel = new JPanel(new FlowLayout(
                FlowLayout.TRAILING, 5, 3));
        rightPanel.setOpaque(false);
        add(rightPanel, BorderLayout.EAST);
    }
 
    public void setLeftComponent(JComponent component) {
        leftPanel.add(component);
    }
 
    public void addRightComponent(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(
                FlowLayout.LEADING, 5, 0));
        panel.add(new JPanel());
        panel.add(component);
        rightPanel.add(panel);
    }
 
}
