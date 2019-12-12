package network;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.nio.ch.Net;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends NetworkPlayer {
    private int port = 50000;
    private String ipAdresse;
    private String line;
    private Socket s;
    private static BufferedReader in;
    private static Writer out;
    private String size;
    private String loadGame = "";


    public Client(){};
    /**
     * Normaler Konstruktor.
     * @param p Port wird übergeben.
     * @param ip IP wird übergeben.
     *
     * Die entsprechenden Attribute des Servers werden im Konstroktur mit den übergebenen Parametern initaliert.
     * Es werden Port, IP und Feldgröße gesetzt.
     */
    public Client(int p, String ip) {
        this.port = p;
        this.ipAdresse = ip;



    }

    /**
     * Funtion um eine Nachricht des Servers per Buffered Reader zu empfangen.
     * @return liefere falls eine Nachricht empfangen wurde, die Nachricht. Andernfalls einen leeren String
     */

    public String inMessage() {
        try {
            this.line = this.in.readLine();
            System.out.println("Client <<< " + line);
            return line;

        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }


    }

    /**
     * Funktion zum Senden von Nachrichten per Writer.
     * @param msg die zu verschickende Nachricht.
     */
    public void outMessage(String msg) {
        try {
            this.line = msg;

            System.out.println("Client >>> " + line);

            out.write(String.format("%s%n", this.line));
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * Diese Funktion erstellt den Socket des Clients. Sie wird im MultiplayerHost nach einem Task aufgerufen,
     * wenn dieser erfolgreich war.
     * Es wird versucht, eine ausgehende Vernindung mit einer angegebenen IP auf einem bestimmten Port
     * herzustellen. Anschließend werden die Ein- und Ausgabeströme initialisiert und die erste Nachricht empfangen.
     * Zuletzt wird noch eine Fallunterscheidung durchgeführt, ob ein neues Spiel gestarter wird(else) oder ein
     * Spiel geladen wird(if). Gesendet wird immer das Attribut line.
     *
     *
     * Es ist nicht möglich, innerhalb eines Netzwerks einen Laptop im W-Lan als Client auf einen hostenden Server
     * ,der per LAN im Netzwerk angebunden ist, zu joinen. (kann möglicherweise an der IP Adresse liegen, da in unseren
     * beiden Testfällen die PCs die gleiche lokale IP hatten.) Die Verbindung von LAN als Client zu WLAN als Server
     * funktioniert komischerweise problemlos.
     */
    public void createSocket() {
        try{
            this.s = new Socket(ipAdresse, port);
            System.out.println("Connection established.");
            in = new BufferedReader((new InputStreamReader(s.getInputStream())));
            out = new OutputStreamWriter(s.getOutputStream());
            line  = in.readLine();
            if(line.contains("LOAD")) {
                this.loadGame = line;
                size = "SIZE 0";
            }else
                size = line;
        }catch(ConnectException e1){
            e1.printStackTrace();
            System.out.println("Fehler");
            closeSockets();
            this.s = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return s;
    }


    /**
     * Get-Methode um die Feldgröße zu erhalten.
     * @return Da die Feldgröße in einem String gespeichert ist und die Funktionen die diesen Wert benötigen
     * einen Integer Wert verlangen, wird zunächst der String aufgeteilt. Dann wird der Teil, in dem sich die
     * Feldgröße als Zahl befindet zu einem Int geparsed und zurückgegeben.
     */

    public int getSize() {
        String[] s = size.split(" ");
        return Integer.parseInt(s[1]);
    }

    public void closeSockets(){
        try {
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setSize(String size) {
        this.size = size;
    }

    public String getLoadGame() {
        return loadGame;
    }
}
