package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import client.Client;
import client.ConnectionException;
import decoder.BDecodeException;

public class MainMenu extends JFrame {

    /**
	 * 
	 */
	int counter;
	public JTextPane pane;
	private static final long serialVersionUID = 1L;
	private String disp = "";
	private JTextField jtfText;
	/*column names for tab sections*/
	private Object columnNamesInfo[] = { "Seeds", "Peers", "Wasted", "Remaining", "Uploaded"};
	private Object columnNames[] = { " # ", "Name", "Size", "Status" };
	private Object columnNamesPeers[] = { "IP", "Client", "Flags", "%", "Down Speed", "Up Speed", "Downloaded"};
	private Object columnNamesTracker[] = { "Name", "Status", "Update In"};
	private Object columnNamesFile[] = { "Path", "Size", "Done", "%", "# Pieces", "Pieces", "Priority"};
	private JTable infoPanel;
	private JTable peersPanel;
	private JTable trackersPanel;
	private JTable filePanel;
	private DefaultTableModel torrentsModel = new DefaultTableModel(columnNames, 0);
	private DefaultTableModel fileModel = new DefaultTableModel(columnNamesFile, 0);
	private DefaultTableModel infoModel = new DefaultTableModel(columnNamesInfo, 0);
	private DefaultTableModel trackersModel = new DefaultTableModel(columnNamesTracker, 0);
	private DefaultTableModel peersModel = new DefaultTableModel(columnNamesPeers, 0);
	private JFileChooser fc;
	private Client client;
	
	public MainMenu(Client clnt) {
        this.client = clnt;
        counter = 0;
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

        /*File Options Help Menu*/
        JMenu fileMenu = new JMenu("File");
        JMenu optionsMenu = new JMenu("Options");
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem newMi = new JMenuItem(new MenuItemAction("New", null, 
                KeyEvent.VK_N));

        JMenuItem openMi = new JMenuItem(new MenuItemAction("Open", null, 
                KeyEvent.VK_O));

        JMenuItem saveMi = new JMenuItem(new MenuItemAction("Save", null, 
                KeyEvent.VK_S));

        JMenuItem exitMi = new JMenuItem("Exit", null);
        
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
        
        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(newMi);
        fileMenu.add(openMi);
        fileMenu.add(saveMi);
        fileMenu.addSeparator();
        fileMenu.add(exitMi);

        menubar.add(fileMenu);
        menubar.add(optionsMenu);
        menubar.add(helpMenu);
        
        /*ToolBar*/
        URL imageUrl = getClass().getResource("../resources/addFile.png");
        JButton button1 = new JButton(new ImageIcon(imageUrl)); 
        button1.addActionListener(new AddFileActionListener()); 
        
        imageUrl = getClass().getResource("../resources/trashF.png");
        JButton button2 = new JButton(new ImageIcon(imageUrl)); 
        button2.addActionListener(new TrashActionListener()); 
        
        imageUrl = getClass().getResource("../resources/searchT.png");
        JButton button3 = new JButton(new ImageIcon(imageUrl)); 
        button3.addActionListener(new SearchActionListener()); 
        
        jtfText = new JTextField(20);
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
        
        /*Left panel with JTree*/
        JTree torrentTree;
        DefaultMutableTreeNode info = new DefaultMutableTreeNode("");
        DefaultMutableTreeNode torrents = new DefaultMutableTreeNode(new JNameIconPanel("Torrents", "../resources/torr.png"));

        DefaultMutableTreeNode featured = new DefaultMutableTreeNode(new JNameIconPanel("Featured", "../resources/favs.png"));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Downloading", "../resources/down.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Seeding", "../resources/seed.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Completed", "../resources/complete.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Active", "../resources/active.png")));
        torrents.add(new DefaultMutableTreeNode(new JNameIconPanel("Inactive", "../resources/inactive.png")));
        info.add(torrents);
        info.add(featured);
        torrentTree = new JTree(info);
        torrentTree.setCellRenderer(new CountryTreeCellRenderer());
 
        
        /* Status Bar */
        JStatusBar statusBar = new JStatusBar();
        JLabel leftLabel = new JLabel(
             "DHT: 178 nodes");
        statusBar.setLeftComponent(leftLabel);
 
        JLabel DTlabel = new JLabel("D:1.4MB T:27.8MB");
        DTlabel.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(DTlabel);
 
        imageUrl = getClass().getResource("../resources/check.png");
        JButton checked = new JButton(new ImageIcon(imageUrl));
        checked.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(checked);
 
        //TODO update data :D
       /* Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4" },
                { "Row2-Column1", "Row2-Column2", "Row2-Column3", "Row1-Column4" } };
         */  
        JTable table = new JTable(torrentsModel);//rowData, columnNames);
        //addToTable(torrentsModel, rowData);
        
        JPanel contentFrame = new JPanel();
        GridLayout layout = new GridLayout(2, 1);
        contentFrame.setLayout(layout);
        
        JScrollPane scrollPane = new JScrollPane(table);
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("../resources/file.png");
        ImageIcon iconI = createImageIcon("../resources/infos.jpg");
        ImageIcon iconT = createImageIcon("../resources/trackers.png");
        ImageIcon iconP = createImageIcon("../resources/peers.png");
        
        filePanel = new JTable(fileModel);
        JScrollPane filePane = new JScrollPane(filePanel);
        tabbedPane.addTab("Files", icon, filePane,"Files");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        Object rowDataInfo[][] = { { "0 of 549 connected", "2 of 604 connected", "1.15MB",
            "inf", "48.0kB"} };
        
    
        infoPanel = new JTable(infoModel);
        addToTable(infoModel, rowDataInfo);
        JScrollPane infoPane = new JScrollPane(infoPanel);
        tabbedPane.addTab("Info", iconI, infoPane, "Info");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
         
        Object rowDataPeers[][] = { { "192.168.78.21", "AlaBala", "CF",
                "10", "90", "23", "56"} };
        
        peersPanel = new JTable(peersModel);
        addToTable(peersModel, rowDataPeers);
        peersPanel.setPreferredSize(new Dimension(410, 50));
        JScrollPane peersPane = new JScrollPane(peersPanel);
        tabbedPane.addTab("Peers", iconP, peersPane,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
         
        Object rowDataTracker[][] = { { "[DHT]", "announcing", "" },
                { "[Local Peer Discovery]", "working", ""} };
        
            
        trackersPanel = new JTable(trackersModel);
        addToTable(trackersModel, rowDataTracker);
        trackersPanel.setPreferredSize(new Dimension(410, 50));
        JScrollPane trackersPane = new JScrollPane(trackersPanel);
        tabbedPane.addTab("Trackers", iconT, trackersPane,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
        
        JPanel jp2 = new JPanel(new BorderLayout());
        
        jp2.add(tabbedPane);
        
        contentFrame.add(scrollPane);
        contentFrame.add(jp2);
        
        add(statusBar, BorderLayout.SOUTH);
        add(torrentTree, BorderLayout.WEST);
        add(contentFrame, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitProcedure();
            }
        });
 
        setJMenuBar(menubar);   
        
    }
    
    /*TODO handler for Files Tab*/
    protected JComponent makeFilesTabbedPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    
    //TODO addToTable 
    protected void addToTable(DefaultTableModel dtm, Object[][] rowData) {
    	for (Object[] row : rowData) {
    		dtm.addRow(row);
    		
    	}
    }
    
    class AddFileActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
        	fc = new JFileChooser();
        	FileNameExtensionFilter filter = new FileNameExtensionFilter("Torrent files", "torrent");
        	fc.setFileFilter(filter);
        	int returnValue = fc.showOpenDialog(null);
        	if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                client.torrent = selectedFile.getName();
                Object rowData[][] = { {counter , selectedFile.getName(), selectedFile.length(), "Downloading..." },
                       };
                addToTable(torrentsModel, rowData);
                
                try {
					client.processTorrent(selectedFile);
				} catch (BDecodeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ConnectionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                System.out.println(selectedFile.getName());
             }else if (returnValue == JFileChooser.CANCEL_OPTION) {
                 System.out.println("canceled");
             }
        }
    }
    
    class TrashActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(null, "OK. Trash clicked");
        }
     }
    
    class SearchActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
        	JOptionPane.showMessageDialog(null, "OK. Search clicked");
        }
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
        System.exit(0);
    }
    
    /*
     * Inner Class TextHandler -obtains the list of torrents (from the web service)
     * which corresponds to input text 
     * */ 
  	private class TextHandler implements ActionListener {

  		public void actionPerformed(ActionEvent e) {
  			if (e.getSource() == jtfText) {
  				disp = "GotLIst of file : " + e.getActionCommand();
  			}
  			JOptionPane.showMessageDialog(null, disp);
  		}
  	}
        
    private class MenuItemAction extends AbstractAction {
        
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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
    

  
}