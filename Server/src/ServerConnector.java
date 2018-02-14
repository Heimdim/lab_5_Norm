import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnector
{
    final static int PORT=8888;
    private DataOutputStream bw;
    private DataInputStream br;
    private ServerSocket serverSocket;

    public ServerLoader getServer() {
        return serverLoader;
    }

    private ServerLoader serverLoader;

    void sendMessage(Socket tempClient,Long id, String message)
    {
        try
        {
            bw = new DataOutputStream(tempClient.getOutputStream());
            bw.writeUTF(message);
            bw.flush();
            System.out.print("Send message to client "+id+"  "+message+"\n");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    String readMessage(Socket socket) throws IOException
    {
        br=new DataInputStream(socket.getInputStream());
       return br.readUTF();
    }

    Socket accept() throws IOException
    {
        return serverSocket.accept();
    }

    void close() throws IOException
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public ServerConnector(ServerLoader serverLoader)
    {
        try
        {
            this.serverLoader=serverLoader;
            serverSocket=new ServerSocket(PORT);
            System.out.println("Server start at " + InetAddress.getLocalHost() + " and listen " + PORT + " port");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
