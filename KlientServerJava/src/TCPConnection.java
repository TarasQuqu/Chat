/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 * @author Taras
 */
public class TCPConnection {
    private Socket socket;
    private Thread receiveThread;
    private BufferedReader in;//BufferedReader to klasa Java, która odczytuje tekst ze strumienia wejściowego
    private BufferedWriter out;//BufferedWriter zapisuje tekst w strumieniu wyjściowym 
    private ConnectionСheck statusListener;//słuchacz zdarzeń
    

    public TCPConnection(ConnectionСheck statusListener, String ipAddr, int  port) throws IOException {
       //stworzyc
        //wewnetrznyj socket
        this(statusListener, new Socket(ipAddr, port));//od jednego konstruktora wywołanie innego
    }
    public TCPConnection(ConnectionСheck statusListener, Socket socket) 
            throws IOException// zaakceptuje gotowy obiekt socketa i tym sockietom 
            //utworzy dla nas połączenie,throws IOException , bo metoda socket.getInputStream() i socket.getOutputStream() 
            //generuje Exception.
    {
        this.statusListener = statusListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));//strumień wejściowy
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8")));//strumien wyjsciowy
        receiveThread = new Thread(() -> {//nowy strumień, który będzie słuchał wszystkich nadchodzących
            try {
                statusListener.IfConnectionReady(TCPConnection.this);//TCPConnection
                while(!receiveThread.isInterrupted())
                    statusListener.GetMsg(TCPConnection.this, in.readLine());
                String msg = in.readLine();//Dostajemy string
            } catch (IOException e) {
                statusListener.IfException(TCPConnection.this,e);
            } finally {
                statusListener.IfDisconnect(TCPConnection.this);
            }
        });
        receiveThread.start();

    }
    @Override
    public String toString() { // dla log
        return "Client with port " + socket.getPort();
    }
    public synchronized void sendMsg(String value)
    {
        try {
            out.write(value + "\r\n");//generuje try,catch
            out.flush();//flush all buffers
        } catch (IOException e) {
            statusListener.IfException(TCPConnection.this,e);//nie udalo sie odprawic,
            Disconnect();
        }
    }

    public synchronized void Disconnect()//zeby przerwac connect
    {
            receiveThread.interrupt();
        try {
            statusListener.IfDisconnect(this);
            socket.close();//generuje try,catch
        } catch (IOException e) {
            statusListener.IfException(TCPConnection.this,e);
        }
    }
}
