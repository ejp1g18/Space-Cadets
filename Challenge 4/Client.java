import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String name;
    public final boolean host;
    
    public Client() {
        host = false;
        Scanner input = new Scanner(System.in);
        System.out.print("Hostname: ");
        String hostname = input.next();
        System.out.print("Port: ");
        int port = input.nextInt();
        System.out.println("");
        try{
            Socket socket = new Socket(hostname, port);
            System.out.println("Connection created");
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (Exception e){}
    }
    
    public Client(int port){
        host = true;
        try{
            Socket socket = new Socket("localhost", port);
            System.out.println("Connection created");
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
        } catch (Exception e){}
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
