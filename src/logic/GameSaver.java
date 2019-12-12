package logic;

import java.io.*;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Eigene Klasse um Spiel zu speichern und zu laden.
 * @author Tomy
 *
 */
public final class GameSaver extends Application {
    private static Stage window;
    private String lastSaved = null;
    private String loadedName = null;

    @Override
    public void start(final Stage stage) {
        window = stage;

        stage.setTitle("File Chooser Sample");
    }

    /**
     * Funktion fuer den Gegner, um Spiel zu speichern.
     * @param objectToSerialise Die Felder von beiden Spielern, die gespeichert werden soll.
     * @param fileName Name, der Datei.
     */
    public void saveFile(Serializable objectToSerialise, String fileName) {
    	
    	//FileChooser wird erstellt
        FileChooser fileChooser = new FileChooser();
        //Titel vom FileChossder Fenster
        fileChooser.setTitle("Save Game");
        //Dateiname wird gesetzt
        fileChooser.setInitialFileName(fileName);

        //popt ein "Save File" file chooser Dialog
        File file = fileChooser.showSaveDialog(window);

        //Hier wird aus der Datei, in die wir speichern, eine .sav Datei
        if (!file.getName().toLowerCase().endsWith(".sav")) {
            file = new File(file.getParentFile(), fileName + ".sav");
        }

        //Hier werden in die Datei beide Spielfelder gespeichert.
        if (file != null) {
            try {
            	//Byte Stream wird erstellt
                ObjectOutputStream oos = (new ObjectOutputStream(new FileOutputStream(file)));
                oos.writeObject(objectToSerialise);
                
                //Hier wird in den Stream geschrieben
                oos.flush();
                //OOS schlie√üen
                oos.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Funktion fuer eigenen Spiler, um Spiel zu speichern.
     * @param objectToSerialise Die Felder von beiden Spielern, die gespeichert werden soll.
     */
    public void saveFile(Serializable objectToSerialise) {
    	
    	//FileChooser wird erstellt
        FileChooser fileChooser = new FileChooser();
        //Titel vom FileChossder Fenster
        fileChooser.setTitle("Save Game");
        //popt ein "Save File" file chooser Dialog
        File file = fileChooser.showSaveDialog(window);

        //Hier wird aus der Datei, in die wir speichern, eine .sav Datei
        if (!file.getName().toLowerCase().endsWith(".sav")) {
            file = new File(file.getParentFile(), file.getName() + ".sav");
            lastSaved = file.getName();
        }

      //Hier werden in die Datei beide Spielfelder gespeichert.
        if (file != null) {
            try {
            	//Byte Stream wird erstellt
                ObjectOutputStream oos = (new ObjectOutputStream(new FileOutputStream(file)));

                oos.writeObject(objectToSerialise);
                //Hier wird in den Stream geschrieben
                oos.flush();
                //OOS schlie√üen
                oos.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Funktion zum Laden eines Spiels
     * @return Beide Felder, die in der Datei gespeichert wurden.
     * @throws IOException
     */
    public Feld[] loadFile() throws IOException {
    	//FileChooser wird erstellt
        FileChooser fileChooser = new FileChooser();
        //popt ein "Open File" file chooser Dialog.
        File file = fileChooser.showOpenDialog(window);
        //Feld Array, um sp‰ter die Felder dort zu speichern
        Feld[] loadedObject = null;

        //Hier werden aus der Datei, die gespeicherten Felder geladen
        if (file != null) {
            try {
            	//Byte Stream wird erstellt
                ObjectInputStream ois = (new ObjectInputStream(new FileInputStream(file)));

                //Feld Objekt wird in den Feld Array gespeichert
                loadedObject = (Feld[]) ois.readObject();

                //OIS schliessen
                ois.close();

                //Test
                System.out.println(loadedObject.toString());

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        loadedName = file.getName();
        //gespeicherten Felder werden zurueck gegeben
        return loadedObject;
    }

    /**
     * Funktion zum Laden eines Spiels
     * @param fileName Name, der Datei.
     * @return Beide Felder, die in der Datei gespeichert wurden.
     * @throws IOException
     */
    public Feld[] loadFile(String fileName) throws IOException {
    	//FileChooser wird erstellt
        FileChooser fileChooser = new FileChooser();
        //popt ein "Open File" file chooser Dialog.
        File file = fileChooser.showOpenDialog(window);      
        //Feld Array, um sp‰ter die Felder dort zu speichern
        Feld[] loadedObject = null;
        fileChooser.setInitialFileName(fileName);

        //Hier werden aus der Datei, die gespeicherten Felder geladen
        if (file != null) {
            try {
            	//Byte Stream wird erstellt
                ObjectInputStream ois = (new ObjectInputStream(new FileInputStream(file)));

                //Feld Objekt wird in den Feld Array gespeichert
                loadedObject = (Feld[]) ois.readObject();

                //OIS schliessen
                ois.close();

                //Test
                System.out.println(loadedObject.toString());

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
        loadedName = file.getName();
        //gespeicherten Felder werden zurueck gegeben
        return loadedObject;
    }

    public String getLastSaved() {
        return lastSaved;
    }

    public String getLoadedName() {
        return loadedName;
    }
}
