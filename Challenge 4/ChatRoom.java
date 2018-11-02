import java.util.Scanner;

public class ChatRoom {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Hosting (y/n): ");
        String s;
        do {
            s = input.next();
            if (s.equals("y")) {
                //get port
                System.out.print("Port: ");
                int port = input.nextInt();
                Server server = new Server(port);
                server.start();
                System.out.println("hmmmm");
                Client c = new Client(port);
            } else if (s.equals("n")) {
                Client c = new Client();
            } else {
                System.out.println("<<Invalid Input>>");
                System.out.println("");
                System.out.println("Hosting (y/n)");
            }
        } while (!s.equals("n") && !s.equals("y"));
    }

}
