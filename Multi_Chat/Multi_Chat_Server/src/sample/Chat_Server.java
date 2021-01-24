package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Chat_Server {

    public static ServersDB serversDB;

    public static void main(String[] args) {

        serversDB = new ServersDB();

        try(ServerSocket serverSocket = new ServerSocket( 5000)) {

            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                ClientThread client = new ClientThread(clientSocket, serversDB);
                client.start();
                client.messagesRetriever.start();
            }
        }
        catch (IOException exception) {

        }
    }

}
