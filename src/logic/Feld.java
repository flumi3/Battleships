package logic;

import java.io.Serializable;

/**
 * Diese Klasse legt ein neues Feld an.
 * <br> Zusaetzlich werden in dieser Klasse auch die Schiffe erstellt. 
 * @author Tomy
 *
 */
public class Feld implements Serializable {
	
	/**
	 * Legt ein neues Feld an.
	 */
	private FieldState[][] feld;
	/**
	 * Feldgroeße
	 */
	int size;
	//Array mit der in einem Spiel zur Verfï¿½gung stehenden Schiffe in Abbï¿½ngigkeit der Feldgrï¿½ï¿½e. 
	//Initial befindet kein Schiff im Array. Je nach Feldgrï¿½ï¿½e wird die Anzahl der Schiffe mittels der Funktion createShips() ins Array eingetragen. 
	//An Index 0 steht dann die Anzahl 5er-Schiffe, an Index 1 die Anzahle der 4er-Schiffe, dann an Index 2 die 3er Schiffe und an Index 3 die 2er Schiffe.
	//Beispielhafte Belegung bei einer Feldgrï¿½ï¿½e von 6 (6x6): {0, 0, 2, 4} -> 0 5er-Schiffe, 0 4er-Schiffe, 2 3er-Schiffe, 4 2er-Schiffe.
	int[] ships = {0, 0, 0, 0};
	/**
	 * Schiffcounter
	 */
	int counter;
	
	
	/**
	 * Ein neues Feld wird angelegt.
	 * <br>Je nach Feldgroeße wird die Anzahl der Schiffe mittels der Funktion createShips() ins Array eingetragen. 
	 *An Index 0 steht dann die Anzahl 5er-Schiffe, an Index 1 die Anzahle der 4er-Schiffe, dann an Index 2 die 3er Schiffe und an Index 3 die 2er Schiffe.
	 *Beispielhafte Belegung bei einer Feldgroeße von 6 (6x6): {0, 0, 2, 4} = 0 5er-Schiffe, 0 4er-Schiffe, 2 3er-Schiffe, 4 2er-Schiffe.
	 * 
	 * @param size Feldgröße
	 */
	public Feld (int size) {
		this.size = size;
		this.feld = new FieldState[this.size][this.size];
		//Feld mit Wasser fï¿½llen.
		for (int i = 0; i < this.size; i++) {
			for( int j = 0; j < this.size; j++) {
				this.feld[i][j] = FieldState.WATER;
			}
		}
		this.createShips();

		counter = 0;
		for (int i = 0; i < ships.length ; i++) {
			if (i == 0) {
				counter += ships[i] * 5;
			} else if (i == 1) {
				counter += ships[i] * 4;
			} else if (i == 2) {
				counter += ships[i] * 3;
			} else if (i == 3) {
				counter += ships[i] * 2;
			}
		}
	}
	
	/**
	 * Getter, der Inhalt (Wasser = 0, Schiff = 1; Treffer Wasser = 2, Treffer Schiff = 3) der angesprochenen Feldposition zurueckgibt.
	 * 
	 * @param zeile Die Zeile im Feld.
	 * @param spalte Die Spalte im Feld.
	 * @return Inhalt des Feldes.
	 */
	public FieldState getFieldstate(int zeile, int spalte) {
		 return feld[zeile][spalte];
	}
	/**
	 * Setter, der Inhalt (Wasser = 0, Schiff = 1; Treffer Wasser = 2, Treffer Schiff = 3) der angesprochenen Feldposition mit "neu" ueberschreibt.
	 * @param zeile Die Zeile im Feld.
	 * @param spalte Die Spalte im Feld.
	 * @param neu Der neue Inhalt des Feldes.
	 */
	public void setFieldstate(int zeile, int spalte, FieldState neu) {
		feld[zeile][spalte] = neu;
	}
	
	/**
	 * Getter fuer Feldgroeße.
	 * @return Groeße des Feldes.
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Getter fuer Schiffsarray "ships".
	 * @param i Der Index im Schiffsarray. 
	 * @return Anzahl von Schiffen an Position i im Array.
	 */
	public int getShips(int i) {
		return this.ships[i];
	}
	
	/**
	 * Setter fuer Schiffsarray "ships", wobei im angesprochende Index i als Uebergabeparameter die Anzahl um 1 erhoeht wird.
	 * @param i Der Index im Schiffsarray.
	 */
	public void addShip(int i) {
		this.ships[i] += 1; 
	}

	/**
	 * Hilfsfunktion, die ein Array mit dem fuer ein Spiel zur Verfuegung stehende Schiffe in Abhaengigkeit der Feldgroeße ermittelt.
	 */
	void createShips() {
	
		//For Schleife beginnt bei 1 und endet bei der festgelegten Feldgrï¿½ï¿½e. 
		//Mit jedem Hochzï¿½hlen wird entsprechend der vorgegebenen Belegung dem Array "ships" ein Schiff hinzugefï¿½gt.
		for(int i = 1; i <= this.size(); i++) { 
			int mod = i % 10;
			
			if( mod == 1 || mod == 2 || mod == 3 || mod == 4 ) { //2er Schiffe werden um eins erhï¿½ht. 2er-Schiffe besitzen im Array "ships" den Index 3.
				this.addShip(3);
			} else if (mod == 5 || mod == 6 || mod == 7) { //3er-Schiffe werden um eins erhï¿½ht. 3er-Schiffe besitzen im Array "ships" den Index 2.
				this.addShip(2);
			} else if ( mod == 8 || mod == 9) { //4er-Schiffe werden um eins erhï¿½ht. 4er-Schiffe besitzen im Array "ships" den Index 1.
				this.addShip(1);
			} else if ( mod == 0 ) { //5er-Schiffe werden um eins erhï¿½ht. 5er-Schiffe besitzen im Array "ships" den Index 0.
				this.addShip(0);
			}
		}
	}

	/**
	 * Aktuelles Feld zu Testzwecken ausgeben.
	 */
	public void printField() {
		System.out.print("___");
		
		for(int l=0; l<this.size(); l++) {
			System.out.print(l + "_" );
		}
		
		System.out.println("");
		
		for(int i=0; i<this.size(); i++) {
			System.out.print(i + "| ");
			for(int j=0; j<this.size(); j++) {
				System.out.print(this.feld[i][j] + " ");
			}
			System.out.println("");
		}
	}
	/**
	 * Ueberpruefung ob alle Schiffe versenkt wurden.
	 * @return Gibt true zurueck, falls alle Schiffe versenkt wurden.
	 * <br>Ansonsten gibt die Funktion false zurueck.
	 */
	public boolean gameOver() {
		if (counter == 0)
			return true;
		return false;
	}

	public boolean isGameOverMultiplayer() {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				if (getFieldstate(y, x) == FieldState.SHIP)
					return false;
			}
		}
		return true;
	}
	/**
	 * Funktion um den Counter zu verringern.
	 */
	public void decrementCounter() {
		counter--;
	}

}

