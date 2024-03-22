import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        LinkedList<Integer> a = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            a.add(i);
        }
        int b = a.indexOf(6);
        System.out.println(b);
        System.out.println(10+": hello world");
    }
}