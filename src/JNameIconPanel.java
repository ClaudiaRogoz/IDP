import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;

class CountryTreeCellRenderer implements TreeCellRenderer {
        private JLabel label;

        CountryTreeCellRenderer() {
            label = new JLabel();
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof JNameIconPanel) {
            	JNameIconPanel country = (JNameIconPanel) o;
            	System.out.println(country.getFlagIcon());
                URL imageUrl = getClass().getResource(country.getFlagIcon());
                if (imageUrl != null) {
                	System.out.println("NOPE");
                    label.setIcon(new ImageIcon(imageUrl));
                }
                
                label.setText(country.getName());
            } else {
                label.setIcon(null);
                label.setText("" + value);
            }
            return label;
        }
    }


public class JNameIconPanel {
	private String name;
    private String flagIcon;

    JNameIconPanel(String name, String flagIcon) {
        this.name = name;
        this.flagIcon = flagIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlagIcon() {
        return flagIcon;
    }

    public void setFlagIcon(String flagIcon) {
        this.flagIcon = flagIcon;
    }
}
