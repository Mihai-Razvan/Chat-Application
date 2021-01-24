package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MessagesRetriever extends Thread{

    ServersDB serversDB;
    public int serverNumber = 0;     //0 inseamna ca nu e in niciun chta
    int numberOfMessagesInServer;
    PrintWriter output;


    public MessagesRetriever(ServersDB serversDB, PrintWriter output)
    {
        this.serversDB = serversDB;
        this.output = output;
    }


    @Override
    public void run() {

        while(true)
        {
            if(serverNumber != 0)
            {
                try {
                    Statement statement = serversDB.getConnection().createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT COUNT (*) FROM Chat_" + Integer.toString(serverNumber));

                    int actualNumberOfMessages = resultSet.getInt(1);
                   // System.out.println(actualNumberOfMessages);
                    if (actualNumberOfMessages > numberOfMessagesInServer) {
                        for (int i = numberOfMessagesInServer + 1; i <= actualNumberOfMessages; i++) {
                            resultSet = statement.executeQuery("SELECT Message FROM Chat_" + Integer.toString(serverNumber)
                                    + " WHERE MessageNumber = " + Integer.toString(i));

                            //      System.out.println( resultSet.getString(1));
                            output.println("CMN: " + resultSet.getString(1));
                        }

                        numberOfMessagesInServer = actualNumberOfMessages;
                    }


                }
                catch (SQLException exception)
                {
                    System.out.println("COULDN'T RETRIEVE MESSAGE");
                }
            }
            else
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException exception){

                }
            }
        }

    }


    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }


    public void setNumberOfMessages(int serverNumber)        //cand se schimba serveru
    {
        try {
            Statement statement = serversDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT (*) FROM Chat_" + Integer.toString(serverNumber));
            numberOfMessagesInServer = resultSet.getInt(1);
        }
        catch (SQLException exception)
        {
            System.out.println("COULDN'T GET NUMBER OF MESSAGES IN SERVER");
        }
    }
}
