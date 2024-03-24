import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;

public class CoordinatorUI extends JFrame{

    private JTable table1;
    private JButton registerNewPeerButton;
    private JButton unregisterPeerButton;
    private JButton updateButton;
    private JPanel rootPanel;
    private JLabel numOfPeersLabel;
    private final Coordinator coordinator;
    CoordinatorUI(Coordinator coordinator) {
        this.coordinator = coordinator;

        this.setContentPane(rootPanel);
        this.setSize(550, 600);
//        this.pack();
        this.setTitle("CoordinatorUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        numOfPeersLabel.setText("num of peers in ring: " + coordinator.getPeers().size());
        registerNewPeerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("registerNewPeerButton is clicked");
                Peer peer = coordinator.registerPeer();
                try {
                    peer.start();
                    peer.setPeerUI(new PeerUI(peer));
                    JOptionPane.showMessageDialog(rootPanel, "peer " + peer.getId() + " registered in port: " + peer.getPort(), "Notify", JOptionPane.INFORMATION_MESSAGE);
                    coordinator.broadcastMessage("coord: peer num " + peer.getId() + " has joined the chat\n");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                updateTable();
            }
        });
        unregisterPeerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(table1.getSelectedRow());
                if(table1.getSelectedRow() == -1){
                    JOptionPane.showMessageDialog(rootPanel, "no chosen Peer from the table!!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Peer peer = (Peer) getTableIndex(4, table1.getSelectedRow());
                    coordinator.unregisterPeer(peer);
                    try {
                        coordinator.broadcastMessage(peer.getId() + ": "+ " has left the ring\n");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(rootPanel, "Unregistered Peer", "Notify", JOptionPane.INFORMATION_MESSAGE);
                }
                updateTable();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });
    }
    public void updateTable(){
        clearTable();
        createTable();
        numOfPeersLabel.setText("num of peers in ring: " + coordinator.getPeers().size());
    }
    public Object getTableIndex(int x, int y){
        DefaultTableModel model = (DefaultTableModel) table1.getModel();

        // Check if the row and column indices are within bounds
        if (model.getRowCount() > y && model.getColumnCount() > x) {
            Object value = model.getValueAt(y, x); // Row 4, Column 3 (zero-based indexing)
            System.out.println("Value at row 5, column 3: " + value);
            return value;
        } else {
            System.out.println("Row or column index out of bounds!");
            return -1;
        }
    }
    public void clearTable(){
        DefaultTableModel dm = (DefaultTableModel)table1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged(); // notifies the JTable that the model has changed
    }

    public void createTable(){
        LinkedList<Peer> peers = this.coordinator.getPeers();
        int size = peers.size();
        Object[][] data = new Object[size][6];

        for(int j = 0; j < size; j++){
            data[j][0] = peers.get(j).getId();
            data[j][1] = peers.get(j).getPort();
            data[j][2] = peers.get(j).getTargetHost();
            data[j][3] = peers.get(j).getTargetPort();
            data[j][4] = peers.get(j);
        }
        table1.setModel(new DefaultTableModel(
                data,
                new String[] {"ID", "Port", "TargetHost", "TargetPort", "Peer"}
        ));
        table1.setAutoCreateRowSorter(true); // sorting of the rows on a particular column
        table1.getColumnModel().getColumn(4).setMaxWidth(0);
        table1.getColumnModel().getColumn(4).setMinWidth(0);
    }
}
