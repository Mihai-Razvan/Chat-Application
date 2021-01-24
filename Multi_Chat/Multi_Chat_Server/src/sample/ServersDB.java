package sample;


import java.security.PublicKey;
import java.sql.*;

public class ServersDB {

    Statement statement;
    Connection connection;

    public ServersDB()
    {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:D:/Mihai/Java-Objects/Java-Projects/Multi_Chat/Multi_Chat_Server/ServersDB.db");
            statement = connection.createStatement();

          //  statement.execute("DROP TABLE Chat_1");
          //  statement.execute("UPDATE Info SET ChatsNumber = 0 WHERE RowNumber = 1");
        }
        catch (SQLException exception) {
            System.out.println("EXCEPTION " + exception.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void writeMessageToServerDB(String message, int serverNumber)
    {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT (*) FROM Chat_" + Integer.toString(serverNumber));

            statement.execute("INSERT INTO Chat_" + Integer.toString(serverNumber) + " VALUES ("
                    + Integer.toString(resultSet.getInt(1) + 1) + ",'" + message + "', 1, '23')");
        }
        catch (SQLException exception) {

        }
    }


    public void createNewChatTable()
    {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT ChatsNumber FROM Info WHERE RowNumber = 1");
            int chatsNumber = resultSet.getInt(1);

            statement.execute("UPDATE Info SET ChatsNumber = " + Integer.toString(chatsNumber + 1) + " WHERE RowNumber = 1");

            statement.execute("CREATE TABLE Chat_" + Integer.toString(chatsNumber + 1)
                    + " (MessageNumber TEXT, Message TEXT, UserID INTEGER, Time TEXT)");
        }
        catch (SQLException exception) {
            System.out.println("COULDN'T CREATE NEW CHAT TABLE");
        }

    }
}
