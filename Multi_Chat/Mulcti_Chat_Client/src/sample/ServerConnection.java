package sample;

import javafx.application.Application;
import org.xml.sax.ext.DeclHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerConnection extends Thread {

    String message;
    BufferedReader input;
    PrintWriter output;

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 5000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("CONNECTED");

            while(true)
            {
                String echo = input.readLine();
                addMessageToList(echo);
            }
        }
        catch (IOException exception) {
            System.out.println("COULDN'T CONNECT");
        }

    }

    public void sendMessage(String message)
    {
        this.message = message;
        output.println(message);
    }

    public void addMessageToList(String message)
    {
        if(message.startsWith("CMO: "))    //mesajele de pe chat
        {
            Main.addOldMessageToList(message.substring(message.indexOf("C") + 5));
        }
        else if(message.startsWith("CMN: "))
        {
            Main.addNewMessageToList(message.substring(message.indexOf("C") + 5));
        }
    }


    public void getMessageFromList()
    {

    }
}
