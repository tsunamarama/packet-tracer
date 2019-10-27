import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private JPanel summaryPanel;
    private PacketGrapher graphPanel;
    private JMenuItem openFile = new JMenuItem("Open Trace File");
    private JMenuItem quit = new JMenuItem("Quit");
    private JMenuItem savePDF = new JMenuItem("Save Graph to PNG");
    private JFileChooser fileChooser = new JFileChooser();
    private File newFile;
    private JRadioButton destHosts = new JRadioButton("Destination Hosts");
    private JRadioButton sourceHosts = new JRadioButton("Source Hosts");
    private JComboBox<Object> hostSelector = new JComboBox<Object>();
    private ArrayList<String> sourceHostList = new ArrayList<String>();
    private ArrayList<String> destHostList = new ArrayList<String>();
    private DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<Object>();
    private JLabel volumeMax = new JLabel();
    private JLabel volumeTotal = new JLabel();
    private JLabel lengthTotal = new JLabel();

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
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mainPanel.add(setupRadioButtons());
        mainPanel.add(setupComboBox());
        mainPanel.add(setupSummaryPanel());
        mainPanel.add(setupGraph());
        return mainPanel;
    }

    private void setupActionListeners() {
        sourceHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostSelector.setModel(new DefaultComboBoxModel<Object>(sourceHostList.toArray()));
                if (comboBoxPanel.isVisible()) {
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                    updateSummaryPanel();
                }
            }
        });
        destHosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostSelector.setModel(new DefaultComboBoxModel<Object>(destHostList.toArray()));
                if (comboBoxPanel.isVisible()) {
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                    updateSummaryPanel();
                }
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
                    newFile = fileChooser.getSelectedFile();
                    currentFile = new TraceFile(newFile);
                    sourceHostList = currentFile.getSourceHostList();
                    destHostList = currentFile.getDestHostList();
                    setupComboLists();
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                    updateSummaryPanel();
                }
            }
        });
        hostSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String addr = (String) hostSelector.getSelectedItem();
                if (addr != null) {
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                    updateSummaryPanel();
                }
            }
            @Override
            public void actionPerformed(ActionEvent a) {
                String addr = (String) hostSelector.getSelectedItem();
                if (addr != null) {
                    graphPanel.updateData(currentFile.hostPackets.get((String) hostSelector.getSelectedItem()));
                    updateSummaryPanel();
                }
            }
        });
        savePDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBoxPanel.isVisible()) {
                    String imagePath = graphPanel.saveGraph(newFile.getParent(), (String) hostSelector.getSelectedItem());
                    String message = "Your graph has been saved here: \n" + imagePath;
                    JOptionPane.showMessageDialog(PacketVisualiser.this, message);
                } else {
                    JOptionPane.showMessageDialog(PacketVisualiser.this, "You must open a file first", "No Graph Available", JOptionPane.WARNING_MESSAGE);
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
        radioButtonPanel.setPreferredSize(new Dimension(175, 100));
        radioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        sourceHosts.setSelected(true);
        return radioButtonPanel;
    }

    private JMenuBar setupMenuBar() {
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(openFile);
        file.add(savePDF);
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

    private JPanel setupSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setPreferredSize(new Dimension(400, 100));
        summaryPanel.setVisible(false);
        summaryPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return summaryPanel;
    }

    private void updateSummaryPanel() {
        volumeMax.setText(String.format("Highest Transmission Volume:     %,d bytes", graphPanel.getMaxBytes()));
        lengthTotal.setText(String.format("Total Transmission Time:               %d seconds (%.2f minutes)", graphPanel.getMaxSeconds(), graphPanel.getMaxSeconds() / 60.0));
        volumeTotal.setText(String.format("Total Transmission Volume:          %,d bytes", graphPanel.getTotalBytes()));
        summaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 35, 12));
        summaryPanel.add(volumeMax);
        summaryPanel.add(lengthTotal);
        summaryPanel.add(volumeTotal);
        summaryPanel.setVisible(true);
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