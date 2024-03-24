import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

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

        textArea1.setEditable(false);
        textArea1.setLineWrap(true);

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
                            Object selectedItem = comboBox1.getSelectedItem();
                            if(selectedItem != "public"){
                                peer.relayMessage(new Message(peer.getId(), text, (int)selectedItem));
                                textArea1.append(peer.getId() + ": " + "(whisper) "+ selectedItem + ": " + text +"\n");
                            }
                            else {
                                peer.relayMessage(new Message(peer.getId(), text));
                                textArea1.append(peer.getId() + ": " + text + "\n");
                            }
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
    public void setComboBox1WithArrayList(ArrayList<Integer> arr){
        comboBox1.removeAllItems();
        comboBox1.addItem("public");
        for(Integer item : arr){
            comboBox1.addItem(item);
        }
    }
}
