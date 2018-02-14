import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerThread extends Thread
{
    private static ServerConnector connector;
    private Socket socket;
    private int id;
    private String getMessage="";
    private String sendMessage="";
    private String clientLogin;

    public String getClientLogin() {
        return clientLogin;
    }

    @Override
    public long getId()
    {
        return id;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public ServerThread(ServerConnector connector,Socket socket, int id) throws IOException
    {
        this.connector=connector;
        this.socket=socket;
        this.id=id;
        start();
    }

    private void validate(String type)
    {
        try
        {
            Map<String,String> base=connector.getServer().getDataBase();
            String getMess,login,password,sendMessage;

            getMess=connector.readMessage(socket);
            String[]words=getMess.split("[,]");
            login=words[0];password=words[1];
            clientLogin=login;

            if(type.equals("Signing up"))
            {
                if(base.get(login)==null)
                {
                    base.put(login,password);
                    sendMessage="Successful";
                }
                else
                    sendMessage="Unsuccessful";
            }
            else
            {
                if(password.equals(base.get(login)))
                    sendMessage="Allowed";
                else
                    sendMessage="Forbidden";
            }
            connector.sendMessage(socket, (long) id,sendMessage);
            connector.getServer().sendLogin(socket,this);
            connector.getServer().setDataBase(base);
            connector.getServer().writeDataBase();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while(!socket.isClosed())
        {
            try
            {
                getMessage = connector.readMessage(socket);
                if(getMessage.equals("Signing in")||getMessage.equals("Signing up"))
                    validate(getMessage);
                else
                    if(!getMessage.equals("disconnect"))
                    {
                        sendMessage = clientLogin + ": " + getMessage + "\r";
                        System.out.print("Get message from client " + id + "  " + getMessage + "\r");
                        connector.getServer().notifyAll(sendMessage,(long)id);
                    }
                    else
                        close();
            } catch (IOException e)
            {
                close();
            }
        }
    }

    private void close()
    {
        try
        {
            socket.close();
            System.out.println("Disconnect client "+id+"\n");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
