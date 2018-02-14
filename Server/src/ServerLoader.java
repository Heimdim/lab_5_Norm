import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerLoader
{
    private ServerConnector connector;
    private List<ServerThread> clients=new ArrayList<>();
    private Map<String,String> dataBase;

    public Map<String, String> getDataBase()
    {
        return dataBase;
    }

    public void setDataBase(Map<String, String> dataBase)
    {
        this.dataBase = dataBase;
    }

    private void readDataBase()
    {
        try
        {
            String path="D:\\Proga\\3kurs\\Java_Sakovich\\lab_5_Norm\\data.bin";
            ObjectInputStream in=new ObjectInputStream(new FileInputStream(path));
            dataBase=(HashMap<String,String>)in.readObject();
            in.close();
        } catch (IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    void writeDataBase()
    {
        try
        {
            ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("D:\\Proga\\3kurs\\Java_Sakovich\\lab_5_Norm\\data.bin"));
            out.writeObject(dataBase);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfTerminated()
    {
        for(int i=0;i<clients.size();i++)
            if(clients.get(i).getState().toString().equals("TERMINATED"))
               return i;
        return -1;
    }

    private void initServer()
    {
        connector=new ServerConnector(this);
        dataBase=new HashMap<>();
        readDataBase();
    }

    private void destroyServer() throws IOException
    {
        connector.close();
    }

    private void handle()
    {
        while(true)
        {
            try
            {
                Socket tempClient=connector.accept();
                ServerThread thread;
                int numberOfTerminatedThread=getNumberOfTerminated();

                if(numberOfTerminatedThread==-1)
                {
                    thread=new ServerThread(connector,tempClient,clients.size()+1);
                    clients.add(thread);
                }
                else
                {
                    thread=new ServerThread(connector,tempClient,numberOfTerminatedThread+1);
                    clients.set(numberOfTerminatedThread,thread);
                }

                sendId(tempClient,thread);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    void notifyAll(String message,Long incomingId)
    {
        for (ServerThread o : clients)
        {
            Long tempId=o.getId();
            if(!incomingId.equals(tempId))
                connector.sendMessage(o.getSocket(),o.getId(),message);
        }
    }
    private void sendId(Socket tempClient,Thread thread)
    {
        connector.sendMessage(tempClient, thread.getId(), String.valueOf(thread.getId()));
    }

    void sendLogin(Socket tempClient,ServerThread thread)
    {
        connector.sendMessage(tempClient,thread.getId(),thread.getClientLogin());
    }

    public static void main(String[] args) throws IOException
    {
        ServerLoader serverLoader=new ServerLoader();
        serverLoader.initServer();
        serverLoader.handle();
        serverLoader.destroyServer();
    }
}
