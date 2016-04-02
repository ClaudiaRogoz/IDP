/**
 * Copyright (c) 2012, Dhilshuk Reddy All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute SwingJD software is freely
 * granted, provided that this notice is preserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * 
 * @author Dhilshuk Reddy
 * 
 */
public class CollapsablePanel extends JPanel {

	private JPanel container = new JPanel();
	//private TopPanel topPanel;
	private String title;
	private int theme;
	private String iconPath;
	private JComponent contentPanel;

	/**
	 * Constructor with title and content panel as paramters.
	 * 
	 * @param title
	 *            label on the CollapsablePanel
	 * @param contentPanel
	 *            panel added to the container
	 */
	public CollapsablePanel(String title, JComponent contentPanel) {
		this.title = title;
		this.contentPanel = contentPanel;
		build();
	}

	/**
	 * Constructor with title ,content panel and theme as paramters.
	 * 
	 * @param title
	 *            label on the CollapsablePanel
	 * @param contentPanel
	 *            panel added to the container
	 * @param theme
	 *            theme of the collapsable Panel
	 */
	public CollapsablePanel(String title, JComponent contentPanel, int theme) {
		this.title = title;
		this.contentPanel = contentPanel;
		this.theme = theme;
		build();
	}

	/**
	 * Constructor with title ,content panel and theme as paramters.
	 * 
	 * @param title
	 *            label on the CollapsablePanel
	 * @param contentPanel
	 *            panel added to the container
	 * @param theme
	 *            theme of the collapsable Panel
	 * @param iconPath
	 *            path of the icon on the collapsable Panel
	 */
	public CollapsablePanel(String title, JComponent contentPanel, int theme,
			String iconPath) {
		this.title = title;
		this.contentPanel = contentPanel;
		this.theme = theme;
		this.iconPath = iconPath;
		build();
	}

	/**
	 * Builds the entire CollapsablePanel
	 */
	private void build() {
		ImageIcon icon = null;
		if (iconPath != null) {
		  icon = new ImageIcon(getClass().getResource(iconPath));
			
		}
		//topPanel = new TopPanel(title, icon, container, theme);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		container.setLayout(new BorderLayout());
		container.add(this.contentPanel, BorderLayout.CENTER);
		container.setVisible(true);
		container.setBackground(new Color(53, 53, 53, 100));
		container.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout());
		//add(topPanel, BorderLayout.NORTH);
		add(container, BorderLayout.CENTER);
		setOpaque(false);
	}

	/**
	 * Returns the TopPanel
	 * 
	 * @return topPanel
	 */
	/*public TopPanel getTopPanel() {
		return topPanel;
	}*/

	/**
	 * Sets the Topanel.
	 * 
	 * @param topPanel
	 *            Customized JPanel
	 */
	/*public void setTopPanel(TopPanel topPanel) {
		this.topPanel = topPanel;
	}*/

}