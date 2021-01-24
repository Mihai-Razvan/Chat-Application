package sample;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ClientThread extends Thread {

    public MessagesRetriever messagesRetriever;
    public ServersDB serversDB;

    public Socket socket;
    public BufferedReader input;
    public PrintWriter output;
    public int serverNumber = 0;           //0 inseamna ca nu e in niciun chat

    public ClientThread(Socket socket, ServersDB serversDB)
    {
        this.socket = socket;
        this.serversDB = serversDB;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("CLIENT CONNECTED");
        }
        catch (IOException exception) {
            System.out.println("CLIENT COULDN'T CONNECT");
        }

        messagesRetriever = new MessagesRetriever(serversDB, output);
    }

    @Override
    public void run() {

        while (true)
        {
            try {
                String echo = input.readLine();
                System.out.println(echo);

                if(echo.startsWith("M: "))
                {
                    serversDB.writeMessageToServerDB(echo.substring(echo.indexOf("M") + 3), serverNumber);
                }
                else if(echo.startsWith("SC: "))
                {
                    serverNumber = Integer.parseInt(echo.substring(echo.indexOf("S") + 4));
                    messagesRetriever.setNumberOfMessages(serverNumber);
                    messagesRetriever.setServerNumber(serverNumber);
                    sendInitialMessages(serverNumber);
                }
                else if (echo.startsWith("CNC"))
                {
                    serversDB.createNewChatTable();
                }
            }
            catch (IOException exception) {

            }
        }
    }


    void sendInitialMessages(int serverNumber)
    {
        try {
            Statement statement = serversDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT (*) FROM Chat_" + Integer.toString(serverNumber));
            int numberOfMessages = resultSet.getInt(1);

            for(int i = numberOfMessages; i >= numberOfMessages - 5 && i != 0; i--)
            {
                resultSet = statement.executeQuery("SELECT Message FROM Chat_" + Integer.toString(serverNumber)
                        + " WHERE MessageNumber = " + Integer.toString(i));

                output.println("CMO: " + resultSet.getString(1));
            //    System.out.println(resultSet.getString(1));
            }
        }
        catch (SQLException exception) {
            System.out.println("COULDN'T WRITE INITIAL MESSAGES LIST");
        }


    }


}
