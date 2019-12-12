package network;

import javafx.scene.text.Text;
import logic.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Klasse zur Stringverarbeitung.
 */
public class NetworkPlayer extends RealPlayer {



    private int lastOutX, lastOutY;

    public NetworkPlayer() {
    }


    /**
     * Funktion zu Stringverarbeitung
     * @param line zu verarbeitende Nachricht.
     * @return Im Falle eines eingehenden Schusses werden die Koordinaten in einem String zurückgegeben.
     * in den anderen defineirten Fällen findet hier keine Verarbeitung statt und es wird die Nachricht zurückgegeben.
     *
     */

    //Eingabestrom aufteilen und verarbeiten
    public String compute(String line) {

        String[] Message = line.split(" ", 2);
        String[] Values;


        String Befehl = Message[0];
        switch (Befehl) {

            case "SIZE":
                return "OK";

            case "SHOT":


                Values = Message[1].split(" ");
                line = Values[0] + " " + Values[1];
                return line;


            case "ANSWER":
                Values = Message[1].split(" ");

                if (Values[0].equals("0")) {
                    System.out.println("Vorbei!");
                    return "ANSWER 0";
                } else {
                    System.out.println("Erneut schiessen");
                    if (Values[0].equals("2")) {
                        return "ANSWER 2";
                    } else {

                        return "ANSWER 1";

                    }
                }


            case "SAVE":

                System.out.println("Spiel speichern");
                return Message[1] + " " + Message[2];


            case "LOAD":

                System.out.println("Spiel laden");
                return Message[1] + " " + Message[2];

            case "OK":
                break;


            default:

                System.out.println("Ungueltige Nachricht");
                break;


        }

        return "UI";

    }



}

