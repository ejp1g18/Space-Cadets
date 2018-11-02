import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread {

    private PrintWriter writer;
    private Socket socket;
    private Client client;

    public WriteThread(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void run() {
        Scanner input = new Scanner(System.in);
        String name;
        if (!client.host){
            System.out.print("Enter your name:");
            name = input.nextLine();
        } else {
            name = "Host";
        }
        client.setName(name);
        writer.println(name);
        String text;
        do {
            text = input.nextLine();
            writer.println(text);
        } while (!text.equals("bye"));
        try {
            socket.close();
        } catch (IOException ex) {
 
            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }

}
