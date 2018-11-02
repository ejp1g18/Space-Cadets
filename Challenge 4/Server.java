import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    
    private ArrayList<String> userNames = new ArrayList<>();
    private ArrayList<UserThread> userThreads = new ArrayList<>();
    private int port;
    
    public Server(int port) {
        this.port = port;
        userNames = new ArrayList<>();
        userThreads = new ArrayList<>();
    }
    
    public void run(){
        //start server
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server setup on port " + port);
            while (true) { //check for more users
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (Exception ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    //message all users
    public void broadcast(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
            if (user != excludeUser) {
                user.sendMessage(message);
            }
        }
    }
    
    public void addUserName(String userName) {
        userNames.add(userName);
    }
    
    public void removeUser(String userName, UserThread user) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(user);
            System.out.println("The user " + userName + " quitted");
        }
    }
    
    public boolean hasUsers(){
        return userNames.size() > 0;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }
    
}
