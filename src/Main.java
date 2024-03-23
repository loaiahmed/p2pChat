import java.io.IOException;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    //todo list
    // private, and fix unregisterPeer();
    public static void main(String[] args) throws IOException {
        Coordinator coordinator = new Coordinator();
        coordinator.start();
        CoordinatorUI coordinatorUI = new CoordinatorUI(coordinator);
    }
}