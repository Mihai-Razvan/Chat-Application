package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    static ServerConnection serverConnection;

    private static float Width = 400;
    private static float Height = 400;

    private static Scene chatScene;
    private static Pane chatPane;
    private static TextField writeMessageTF;
    private static Button sendMessageButton;
    public static ArrayList<Label> messagesLabel;

    private static Scene mainScene;
    private static Pane mainPane = new Pane();
    private static Button chat1;
    private static Button chat2;
    private static Button newChatButton;

    private static int maxRowLength = 20;
    private static int rowSize = 15;
    private static int messagesSpacing = 30;
    private static ArrayList<Integer> rowsNumberArray = new ArrayList<Integer>();

    @Override
    public void start(Stage stage) throws Exception {

        messagesLabel = new ArrayList<Label>();

        createChatScene(stage);
        createMainScene(stage);

        stage.setTitle("Chat");
        stage.setScene(mainScene);

        stage.show();
    }


    public static void main(String[] args) {

        serverConnection = new ServerConnection();
        serverConnection.start();

        Application.launch(args);
    }



    void createChatScene(Stage stage)
    {
        writeMessageTF = new TextField();
        writeMessageTF.setLayoutX(100);
        writeMessageTF.setLayoutY(350);
        writeMessageTF.setScaleX(2);
        writeMessageTF.setScaleY(2);

        sendMessageButton = new Button("Send");
        sendMessageButton.setLayoutY(350);
        sendMessageButton.setLayoutX(340);
        sendMessageButton.setOnAction(event -> sendMessage());

        chatPane = new Pane();
        chatPane.getChildren().addAll(writeMessageTF, sendMessageButton);

        chatScene = new Scene(chatPane, Width, Height);
    }


    void createMainScene(Stage stage)
    {
        chat1 = new Button("Chat 1");
        chat1.setOnAction(event -> handle(event, stage));
        chat2 = new Button("Chat 2");
        chat2.setOnAction(event -> handle(event, stage));
        chat2.setLayoutX(100);

        newChatButton = new Button("New Chat");
        newChatButton.setLayoutX(200);
        newChatButton.setLayoutY(200);
        newChatButton.setOnAction(event -> handle(event, stage));

        mainPane.getChildren().addAll(chat1, chat2, newChatButton);
        mainScene = new Scene(mainPane, Width, Height);

    }


    void sendMessage()
    {
        if(!writeMessageTF.getText().isBlank())
        {
            serverConnection.sendMessage("M: " + writeMessageTF.getText());
            writeMessageTF.clear();
        }
    }


    void handle(ActionEvent event, Stage stage)
    {
        if(event.getSource() == chat1)
        {
            serverConnection.sendMessage("SC: " + "1");
            stage.setScene(chatScene);
        }
        else if (event.getSource() == chat2)
        {
            serverConnection.sendMessage("SC: " + "2");
            stage.setScene(chatScene);
        }
        else if (event.getSource() == newChatButton)
            serverConnection.sendMessage("CNC");
    }


    public static void addOldMessageToList(String message)
    {
        Platform.runLater(()->{

            String[] wordsInMessage = message.split(" ");
            int rowsNumber = 1;
            double position;

            Label label = new Label(wordsInMessage[0]);   //primu se pune separat ca sa nu se mai puna spaceu inainte

            int rowLength = 0;

            for(int i = 1; i < wordsInMessage.length; i ++)
            {
                int wordLength = wordsInMessage[i].length();

                if(rowLength + wordLength + 1 <= maxRowLength)      //+1 pt spatiu
                {
                    label.setText(label.getText() + " " + wordsInMessage[i]);
                    rowLength += wordLength;
                }
                else
                {
                    label.setText(label.getText() + "\n" + wordsInMessage[i]);
                    rowLength = wordLength;
                    rowsNumber++;
                }
            }

            rowsNumberArray.add(rowsNumber);

            label.setFont(new Font("Arial", 15));
            label.setLayoutX(200);

            if(messagesLabel.size() == 0)
            {
                position = 350 - (rowsNumber + 2) * rowSize;
                label.setLayoutY(position);
            }
            else
            {
                position = messagesLabel.get(messagesLabel.size() - 1).getLayoutY() - messagesSpacing - (rowsNumber + 2) * rowSize;
                label.setLayoutY(position);
            }

            chatPane.getChildren().add(label);
            messagesLabel.add(label);

        });

    }


    public static void addNewMessageToList(String message)
    {
        Platform.runLater(()->{

            String[] wordsInMessage = message.split(" ");
            int rowsNumber = 1;
            double position;

            Label label = new Label(wordsInMessage[0]);   //primu se pune separat ca sa nu se mai puna spaceu inainte

            int rowLength = 0;

            for(int i = 1; i < wordsInMessage.length; i ++)
            {
                int wordLength = wordsInMessage[i].length();

                if(rowLength + wordLength + 1 <= maxRowLength)      //+1 pt spatiu
                {
                    label.setText(label.getText() + " " + wordsInMessage[i]);
                    rowLength += wordLength;
                }
                else
                {
                    label.setText(label.getText() + "\n" + wordsInMessage[i]);
                    rowLength = wordLength;
                    rowsNumber++;
                }
            }

            if(rowsNumberArray.size() == 0)      //daca nu pun cond asta imi da eroare daca nu exista niciun mesaj in chat
                rowsNumberArray.add(rowsNumber);
            else
                rowsNumberArray.add(rowsNumber, 0);


            label.setFont(new Font("Arial", 15));
            position = 350 - (rowsNumber + 2) * rowSize;
            label.setLayoutX(200);
            label.setLayoutY(position);

            for(int i = 0; i < messagesLabel.size(); i ++)
            {
                if(i == 0)
                {
                    position = position - messagesSpacing - (rowsNumberArray.get(i) + 2) * rowSize;
                    messagesLabel.get(i).setLayoutY(position);
                }
                else
                {
                    position = messagesLabel.get(i - 1).getLayoutY() - messagesSpacing - (rowsNumberArray.get(i) + 2) * rowSize;
                    messagesLabel.get(i).setLayoutY(position);
                }

            }

            chatPane.getChildren().add(label);
            messagesLabel.add(0, label);

        });
    }


}
