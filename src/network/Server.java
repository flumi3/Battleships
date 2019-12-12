package network;
import gui.MultiPlayerHost;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;


public class Server extends Client{
    /**
     * @port Port des des Servers.
     * @ipAdresse IP auf die sich der Servers verbinden soll.
     * @line Variable zur Nachrichtenverarbeitung
     * @s Socket des  Servers.
     * @in, @out Ein- und Ausgabestr�me des Servers.
     * @size Feldgr��e
     * @ss Server Socket in Variable wird zum schlie�en des Servers ben�tigt
     * @loaded wird f�r den Fall, dass ein Spiel geladen werden soll ben�tigt.
     */
    private int port;
    private String ipAdresse;
    private String line;
    private Socket s;
    private BufferedReader in;
    private Writer out;
    private String size;
    private ServerSocket ss;
    private String loaded ="";


    /**
     * Normaler Konstruktor.
     * @param p Port wird �bergeben.
     * @param ip IP wird �bergeben.
     * @param sz Feldgr��e wird �bergeben.
     *
     * Die entsprechenden Attribute des Servers werden im Konstroktur mit den �bergebenen Parametern initaliert.
     * Es werden Port, IP und Feldgr��e gesetzt.
     */
    public Server(int p, String ip, String sz){            //Server Konstruktor
        this.port = p;                          //Port festlegen
        this.ipAdresse = ip;                    //IP festlegen(ueberfluessig?)
        this.size = sz;
        System.out.println(size);
    }

    /**
     * �berladener Konstruktor f�r den Fall, dass das Spiel geladen wird.
     * @param p s.o.
     * @param ip s.o.
     * @param sz s.o.
     * @param ld Dieser Parameter enth�lt den Befehl "LOAD " und den Namen der zu ladenden Datei als String.
     *
     * Werte der Parameter werden den entsprechenden Attributen des Servers zugewiesen, �hnlich wie beim Normalen
     * Konstruktor
     */
    public Server(int p, String ip, String sz, String ld){            //Server Konstruktor
        this.port = p;                          //Port festlegen
        this.ipAdresse = ip;                    //IP festlegen(ueberfluessig?)
        this.size = sz;
        this.loaded = ld;
        this.ss = null;
        this.s = null;
        System.out.println(size);
    }

    /**
     * Funtion um eine Nachricht des Clients per Buffered Reader zu empfangen.
     * @return liefere falls eine Nachricht empfangen wurde, die Nachricht. Andernfalls einen leeren String
     */

    public String inMessage() {
        try {
            this.line = this.in.readLine();                     //Nachricht empfangen
            System.out.println("Server <<< " + line);             //AUSGABE der Nachricht TODO: entfernen
            return line;

        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
                                                      //Wenn leer, Null

    }

    /**
     * Funktion zum Senden von Nachrichten per Writer.
     * @param msg die zu verschickende Nachricht.
     */
    public void outMessage(String msg) {
        try {
            this.line = msg;

            System.out.println("Server >>> " + line);

            out.write(String.format("%s%n", this.line));
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }



    public ServerSocket getSs() {
        return ss;
    }

    public Socket getSocket() {
        return s;
    }

    /**
     * Diese Funktion erstellt den Serversocket. Sie wird im MultiplayerHost nach einem Task aufgerufen, wenn dieser
     * erfolgreich war.
     * Es wird versucht, eine eingehende Verbindung zu akzeptieren. Anschlie�end werden die Ein- und Ausgabestr�me
     * initialisiert.
     * Zuletzt wird noch eine Fallunterscheidung durchgef�hrt, ob ein neues Spiel gestarter wird(else) oder ein
     * Spiel geladen wird(if). Gesendet wird immer das Attribut line.
     */
    public void createSocket(){
        try{
            System.out.println("Waiting for client connection...");
            ss = new ServerSocket(port);
            s = ss.accept();
            System.out.println("Connection established.");
            in = new BufferedReader((new InputStreamReader(s.getInputStream())));
            out = new OutputStreamWriter(s.getOutputStream());
            if(loaded == "")
                line  = size;
            else
                line = loaded;
            out.write(String.format("%S%n", line));
            out.flush();
        }catch(java.lang.Exception e1){
            e1.printStackTrace();
        }
    }

    /**
     * Get-Methode um die Feldgr��e zu erhalten.
     * @return Da die Feldgr��e in einem String gespeichert ist und die Funktionen die diesen Wert ben�tigen
     * einen Integer Wert verlangen, wird zun�chst der String aufgeteilt. Dann wird der Teil, in dem sich die
     * Feldgr��e als Zahl befindet zu einem Int geparsed und zur�ckgegeben.
     */
    public int getSize() {
        String[] s = size.split(" ");
        return Integer.parseInt(s[1]);
    }

    public void closeSockets(){
        try {
            this.s.close();
            this.ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }
}

