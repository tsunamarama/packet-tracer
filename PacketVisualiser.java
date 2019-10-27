import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * @author Michael Johnson
 */
public class PacketVisualiser extends JFrame {
    private static final long serialVersionUID = 1L;
    private TraceFile currentFile;
    private JPanel comboBoxPanel;
    private PacketGrapher graphPanel;
    private JMenuItem openFile = new JMenuItem("Open Trace File");
    private JMenuItem quit = new JMenuItem("Quit");
    private JFileChooser fileChooser = new JFileChooser();
    private JRadioButton destHosts = new JRadioButton("Destination Hosts");
    private JRadioButton sourceHosts = new JRadioButton("Source Hosts");
    private JComboBox<Object> hostSelector = new JComboBox<Object>();
    private ArrayList<String> sourceHostList = new ArrayList<String>();
    private ArrayList<String> destHostList = new ArrayList<String>();
    private DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>();

    /**
     * Creates an instance of a PacketVisualiser object on the EDT.
     * @param args command line arguments not used
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 new PacketVisualiser();
            }
        });
    }

    /**
     * Constructs a <code>PacketVisualiser</code> object with default values.
     * @author Michael Johnson
     */
    private PacketVisualiser() {
        super("Network Packet Transmission Visualiser");
        setSize(1000, 500);
        setupActionListeners();
        setJMenuBar(setupMenuBar());
        add(setupMainPanel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mainPanel.add(setupRadioButtons());
        mainPanel.add(setupComboBox());
        mainPanel.add(setupGraph());
        return mainPanel;
    }

    private void setupActionListeners() {
        sourceHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostSelector.setModel(new DefaultComboBoxModel<Object>(sourceHostList.toArray()));
            }
        });
        destHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostSelector.setModel(new DefaultComboBoxModel<Object>(destHostList.toArray()));
            }
        });
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceHostList.clear();
                destHostList.clear();
                hostSelector.removeAllItems();
                int returnVal = fileChooser.showOpenDialog(PacketVisualiser.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File newFile = fileChooser.getSelectedFile();
                    currentFile = new TraceFile(newFile);
                    sourceHostList = currentFile.getSourceHostList();
                    destHostList = currentFile.getDestHostList();
                    setupComboLists();
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                }
            }
        });
        hostSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String addr = (String) hostSelector.getSelectedItem();
                if (addr != null) {
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                }
            }
        });
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private JPanel setupRadioButtons() {
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(sourceHosts);
        radioButtonGroup.add(destHosts);
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.add(sourceHosts);
        radioButtonPanel.add(destHosts);
        radioButtonPanel.setPreferredSize(new Dimension(200, 100));
        radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        sourceHosts.setSelected(true);
        return radioButtonPanel;
    }

    private JMenuBar setupMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(openFile);
        file.add(quit);
        menu.add(file);
        return menu;
    }

    private JPanel setupComboBox() {
        comboBoxPanel = new JPanel();
        comboBoxPanel.add(hostSelector);
        comboBoxPanel.setVisible(false);
        hostSelector.setPreferredSize(new Dimension(250, 25));
        comboBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return comboBoxPanel;
    }

    private JPanel setupGraph() {
        graphPanel = new PacketGrapher();
        graphPanel.setPreferredSize(new Dimension(995, 350));
        graphPanel.setBackground(Color.WHITE);
        return graphPanel;
    }

    private void setupComboLists() {
        Comparator<String> ipAddr = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] addr1 = o1.split("\\.");
                String fAddr1 = String.format("%3s.%3s.%3s.%3s", addr1[0], addr1[1], addr1[2], addr1[3]);
                String[] addr2 = o2.split("\\.");
                String fAddr2 = String.format("%3s.%3s.%3s.%3s", addr2[0], addr2[1], addr2[2], addr2[3]);
                return fAddr1.compareTo(fAddr2);
            }
        };
        sourceHostList.sort(ipAddr);
        destHostList.sort(ipAddr);
        if (sourceHosts.isSelected()) {
            model = new DefaultComboBoxModel<Object>(sourceHostList.toArray());
        } else if (destHosts.isSelected()) {
            model = new DefaultComboBoxModel<Object>(destHostList.toArray());
        }
        hostSelector.setModel(model);
        comboBoxPanel.setVisible(true);
    }
}