import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PeerUI extends JFrame{
    private JTextArea textArea1;
    private JTextField textField1;
    private JButton sendMessegeButton;
    private JComboBox comboBox1;
    private JPanel rootPanel;
    private JLabel title;
    private Peer peer;

    public PeerUI(Peer peer) {
        this.peer = peer;

        this.setContentPane(rootPanel);
        this.setSize(400, 600);
//        this.pack();
        this.setTitle("peer " + peer.getId());
        title.setText("peer " + peer.getId());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);

        sendMessegeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField1.getText();
                if(!text.replaceAll("\\s", "").equals("")){
                    try {
                        if(text.equalsIgnoreCase("exit")){
                            if(!peer.leave()){
                                JOptionPane.showMessageDialog(rootPanel, "Error 1: " + "Unregister failed", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else if(peer.getTargetHost() != null && peer.getTargetPort() > 0){
                            peer.relayMessage(new Message(peer.getId(), peer.getId() + ": "+ text));
                            textArea1.append(peer.getId() + ": " + text +"\n");
                        }
                        else{
                            JOptionPane.showMessageDialog(rootPanel, "Error 2: " + "no target assigned", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(rootPanel, "Error 3: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }
    public void appendToTextArea(String text){
        textArea1.append(text);
    }
}
