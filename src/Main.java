import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    //todo list
    // private, and fix unregisterPeer();
    public static void main(String[] args) throws IOException {
        Coordinator coordinator = new Coordinator();
        CoordinatorUI coordinatorUI = new CoordinatorUI(coordinator);
        coordinator.start();
    }
}