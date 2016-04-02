import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import java.text.SimpleDateFormat;

public class MainMenu extends JFrame {

    /**
	 * 
	 */
	public JTextPane pane;
	private static final long serialVersionUID = 1L;
	protected TimerThread timerThread;
	String disp = "";
	JTextField jtfText;
	
	public MainMenu() {
        
        initUI();
    }

    private void initUI() {

        createMenuBar();

        setTitle("My fancy Torrent Client");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    private void createMenuBar() {
        
        JMenuBar menubar = new JMenuBar();
        
        ImageIcon iconNew = new ImageIcon("new.png");
        ImageIcon iconOpen = new ImageIcon("open.png");
        ImageIcon iconSave = new ImageIcon("save.png");
        ImageIcon iconExit = new ImageIcon("exit.png");

        JMenu fileMenu = new JMenu("File");
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");
        
        URL imageUrl = getClass().getResource("addFile.png");
        JButton button1 = new JButton(new ImageIcon(imageUrl)); 
        imageUrl = getClass().getResource("trashF.png");
        JButton button2 = new JButton(new ImageIcon(imageUrl)); 
        imageUrl = getClass().getResource("searchT.png");
        JButton button3 = new JButton(new ImageIcon(imageUrl)); 
        jtfText = new JTextField(30);
        JToolBar bar = new JToolBar(); 
        bar.add(button1);
        bar.addSeparator();
        bar.add(button2); 
        bar.addSeparator();
        bar.add(jtfText);
        bar.add(button3); 
        bar.addSeparator();
        bar.setBackground(Color.lightGray);
        
        jtfText.addActionListener(new TextHandler());
        
        JTextArea edit = new JTextArea(8, 40); 
        JScrollPane scroll = new JScrollPane(edit); 
        JPanel pane = new JPanel(); 
        BorderLayout border = new BorderLayout(); 
        pane.setLayout(border); 
        pane.add("North", bar); 
        pane.add("Center", scroll); 
        setContentPane(pane);
        
        JTree torrentTree;
        DefaultMutableTreeNode info = new DefaultMutableTreeNode("");
        DefaultMutableTreeNode torrents = new DefaultMutableTreeNode(new JNameIconPanel("Torrents", "torr.png"));

        DefaultMutableTreeNode featured = new DefaultMutableTreeNode(new JNameIconPanel("Featured", "favs.png"));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Downloading", "down.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Seeding", "seed.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Completed", "complete.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Active", "active.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Inactive", "inactive.png")));
        info.add(torrents);
        info.add(featured);
        torrentTree = new JTree(info);
        torrentTree.setCellRenderer(new CountryTreeCellRenderer());
        CollapsablePanel leftPanel = new CollapsablePanel("Hello", torrentTree);
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem newMi = new JMenuItem(new MenuItemAction("New", iconNew, 
                KeyEvent.VK_N));

        JMenuItem openMi = new JMenuItem(new MenuItemAction("Open", iconOpen, 
                KeyEvent.VK_O));

        JMenuItem saveMi = new JMenuItem(new MenuItemAction("Save", iconSave, 
                KeyEvent.VK_S));

        JMenuItem exitMi = new JMenuItem("Exit", iconExit);
        exitMi.setMnemonic(KeyEvent.VK_E);
        exitMi.setToolTipText("Exit application");
        exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
            ActionEvent.CTRL_MASK));

        exitMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        fileMenu.add(newMi);
        fileMenu.add(openMi);
        fileMenu.add(saveMi);
        fileMenu.addSeparator();
        fileMenu.add(exitMi);

        menubar.add(fileMenu);
        menubar.add(optionsMenu);
        menubar.add(helpMenu);
        
        JStatusBar statusBar = new JStatusBar();
        JLabel leftLabel = new JLabel(
             "Your application is running.");
        statusBar.setLeftComponent(leftLabel);
 
        final JLabel dateLabel = new JLabel();
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(dateLabel);
 
        final JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(timeLabel);
 
        //TODO update data :D
        Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4" },
                { "Row2-Column1", "Row2-Column2", "Row2-Column3", "Row1-Column4" } };
            Object columnNames[] = { " # ", "Name", "Size", "Status" };
        JTable table = new JTable(rowData, columnNames);
        JPanel contentFrame = new JPanel();
        GridLayout layout = new GridLayout(2, 1);
        contentFrame.setLayout(layout);
        
        JScrollPane scrollPane = new JScrollPane(table);
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("file.png");
        ImageIcon iconI = createImageIcon("infos.jpg");
        ImageIcon iconT = createImageIcon("trackers.png");
        ImageIcon iconP = createImageIcon("peers.png");
        JComponent panel1 = makeTextPanel("Files");
        tabbedPane.addTab("Files", icon, panel1,
                          "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        Object rowDataInfo[][] = { { "0 of 549 connected", "2 of 604 connected", "1.15MB",
            "inf", "48.0kB"} };
        Object columnNamesInfo[] = { "Seeds", "Peers", "Wasted", "Remaining", "Uploaded"};
    
        JTable panel2 = new JTable(rowDataInfo, columnNamesInfo);
        JScrollPane scrollPaneInfo = new JScrollPane(panel2);
        tabbedPane.addTab("Info", iconI, scrollPaneInfo,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
         
        Object rowDataPeers[][] = { { "192.168.78.21", "AlaBala", "CF",
                "10", "90", "23", "56"} };
        Object columnNamesPeers[] = { "IP", "Client", "Flags", "%", "Down Speed", "Up Speed", "Downloaded"};
        JTable panel3 = new JTable(rowDataPeers, columnNamesPeers);
        panel3.setPreferredSize(new Dimension(410, 50));
        JScrollPane scrollPanePeers = new JScrollPane(panel3);
        tabbedPane.addTab("Peers", iconP, scrollPanePeers,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
         
        Object rowDataTracker[][] = { { "[DHT]", "announcing", "" },
                { "[Local Peer Discovery]", "working", ""} };
        Object columnNamesTracker[] = { "Name", "Status", "Update In"};
            
        JTable panel4 = new JTable(rowDataTracker, columnNamesTracker);
        panel4.setPreferredSize(new Dimension(410, 50));
        JScrollPane scrollPaneTracker = new JScrollPane(panel4);
        tabbedPane.addTab("Trackers", iconT, scrollPaneTracker,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
        
        JPanel jp2 = new JPanel(new BorderLayout());
        
        jp2.add(tabbedPane);
        
        contentFrame.add(scrollPane);
        contentFrame.add(jp2);
        
        add(statusBar, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);
        add(contentFrame, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });
 
        timerThread = new TimerThread(dateLabel, timeLabel);
        timerThread.start();
        setJMenuBar(menubar);        
    }
    
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainMenu.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    public void exitProcedure() {
        timerThread.setRunning(false);
        System.exit(0);
    }
    
  //Inner Class TextHandler
  	private class TextHandler implements ActionListener {

  		public void actionPerformed(ActionEvent e) {
  			if (e.getSource() == jtfText) {
  				disp = "text1 : " + e.getActionCommand();
  			}
  			JOptionPane.showMessageDialog(null, disp);
  		}
  	}
  	
    public class TimerThread extends Thread {
    	 
        protected boolean isRunning;
 
        protected JLabel dateLabel;
        protected JLabel timeLabel;
 
        protected SimpleDateFormat dateFormat = 
                new SimpleDateFormat("EEE, d MMM yyyy");
        protected SimpleDateFormat timeFormat =
                new SimpleDateFormat("h:mm a");
 
        public TimerThread(JLabel dateLabel, JLabel timeLabel) {
            this.dateLabel = dateLabel;
            this.timeLabel = timeLabel;
            this.isRunning = true;
        }
 
        @Override
        public void run() {
            while (isRunning) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Calendar currentCalendar = 
                            Calendar.getInstance();
                        Date currentTime = 
                            currentCalendar.getTime();
                        dateLabel.setText(dateFormat
                            .format(currentTime));
                        timeLabel.setText(timeFormat
                            .format(currentTime));
                    }
                });
 
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                }
            }
        }
 
        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
 
    }
    private class MenuItemAction extends AbstractAction {
        
        public MenuItemAction(String text, ImageIcon icon, 
                Integer mnemonic) {
            super(text);
            
            putValue(SMALL_ICON, icon);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
            System.out.println(e.getActionCommand());
        }
    }
    
    class DemoAction extends AbstractAction {

        /**
    	 * 
    	 */
    	private static final long serialVersionUID = 1L;

    	public DemoAction(String text, Icon icon, String description,
            char accelerator) {
          super(text, icon);
          putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator,
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
          putValue(SHORT_DESCRIPTION, description);
        }

        public void actionPerformed(ActionEvent e) {
          try {
            pane.getStyledDocument().insertString(0,
                "Action [" + getValue(NAME) + "] performed!\n", null);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                MainMenu ex = new MainMenu();
                ex.setVisible(true);
            }
        });
    }
}