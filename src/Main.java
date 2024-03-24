import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JFrame{
    //todo list
    // private, and fix unregisterPeer();
    public static void main(String[] args) throws IOException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
//        javax.swing.plaf.nimbus.NimbusLookAndFeel
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Coordinator coordinator = new Coordinator();
        CoordinatorUI coordinatorUI = new CoordinatorUI(coordinator);
        coordinator.start();
    }
}