package logic;

import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.*;

public class RealPlayer extends Spieler {
	/**
	 *  Speichert die Koordinaten, des jeweils letzten platzierten Schiffs, der jeweiligen Sorte.
	 */
	private int[] last2 = new int[2];
	private int[] last3 = new int[2];
	private int[] last4 = new int[2];
	private int[] last5 = new int[2];

	/**
	 * Speicher, der innerhalb der Funktion RealPlayer.placeShip() verwendet wird und angibt, welche Schiffe noch nicht platziert wurden.
	 */
	int shipsLeft[] = new int[4]; 
	/**
	 * Variable, die angibt, ob durch den letzten Treffer ein Schiff versenkt wurde.
	 */
	private boolean versenkt; 

	/**
	 * Funktion zum Schiessen. 
	 * @param z Die Zeile des Feldes, auf das geschossen wird.
	 * @param s Die Spalte des Feldes, auf das geschossen wird.
	 * @param zielfeld Das Feld, auf das geschossen wird.
	 * @return Gibt im Falle eines Schiffstreffer true zurück, sonst false.
	 */
		public boolean shoot (int z, int s, Feld zielfeld) {
			versenkt = false;

			System.out.println("Schuss auf Stelle (" + z + ", " + s + ")");//TEST
			
			FieldState zielstatus = zielfeld.getFieldstate(z,s); //Wert aus Array wird der Variable uebergeben 
			
			//Wenn Ziel ein leeres Feld ist, wird dieses mit 2 markiert
			if (zielstatus == FieldState.WATER) {
				System.out.println("Kein Treffer!");//TEST
				zielfeld.setFieldstate(z,s,FieldState.WATER_HIT);
			}
			
			//Wenn Ziel ein Schiff ist, wird dieses mit 3 markiert
			if (zielstatus == FieldState.SHIP) {
				System.out.println("Treffer!");//TEST
				zielfeld.setFieldstate(z,s,FieldState.SHIP_HIT);
				if(this.versenktInfo(zielfeld, z, s) != 0) {
					versenkt = true;
					System.out.println("Versenkt!");//TEST
				}
				zielfeld.decrementCounter();
				return true;
			}
			return false;
		}
		
		/**
		 * Funktion zum Platzieren eines Schiffs.
		 * <br> Falls die Uebergebenen Koordinaten als Feldstatus Wasser beinhalten, wird das Schiff zuerst immer horizontal platziert, falls möglich.
		 * <br> Falls dies nicht möglich ist, wird versucht das Schiff vertikal zu platzieren.
		 * <br> Falls die Uebergebenen Koordinaten ein Schiff beinhalten, wird versucht dieses Schiff zu drehen, falls möglich.	
		 * @param y Die Zeile, von der aus das Schiff platziert werden soll.
		 * @param x Die Spalte, von der aus das Schiff platziert werden soll.
		 * @param shipSize Die Groesse des Schiffs, das platziert werden soll.
		 * @param feld Das Feld, auf das das Schiff platziert werden soll.
		 * @return True, falls das Schiff erfolgreich platziert werden konnte. False, wenn nicht.
		 */
	public boolean placeShip(int y, int x, int shipSize, Feld feld) {
		boolean possible;
		if(feld.getFieldstate(y, x) == FieldState.WATER) {//Es wurde auf Wasser geklickt, also wird versucht das Schiff neu zu platzieren.
			if(this.shipsEmpty(shipSize)) return false; //Kein Schiff der gewaehlten Groesse mehr vorhanden.
			possible = this.spaceForShip(y, x, 'h', shipSize, feld, 'p', FieldState.SHIP); //Pruefen, ob in vertikal Richtung Platz für das Schiff vorhanden ist.
			if(possible) {//Fals Platz da ist, Schiff vertikal platzieren.
				for(int l = 0; l<shipSize; l++) {
					feld.setFieldstate(y, x + l, FieldState.SHIP);
				}//end for
				this.reduceShip(shipSize); //Schiff aus dem Speicher reduzueren
				lastPlaced(y, x, shipSize);
				return true;
			}else {
				//Pruefen, ob in horizontaler Richtung Platz für das Schiff vorhanden ist.
				possible = this.spaceForShip(y, x, 'v', shipSize, feld, 'p', FieldState.SHIP);
				if(possible) {
					for(int l = 0; l<shipSize; l++) {
						feld.setFieldstate(y + l, x, FieldState.SHIP); //Fals Platz da ist, Schiff horizontal platzieren.
					}//end for
					this.reduceShip(shipSize); //Schiff aus dem Speicher reduzieren
					lastPlaced(y, x, shipSize);
					return true;
				}
			}
			return false;
		}
		if(feld.getFieldstate(y, x) == FieldState.SHIP) {//Es wurde auf ein Schiff geklickt, also wird versucht dieses Schiff zu drehen.
			int zeile = y;
			int spalte = x;
			shipSize = 0;
			//aktuelle Ausrichtung (horizintal oder vertikal) des Schiffs ermitteln:
			if( (zeile-1 >= 0 && feld.getFieldstate(zeile-1, spalte) == FieldState.SHIP) || (zeile+1 < feld.size() && feld.getFieldstate(zeile+1, spalte) == FieldState.SHIP) ) {
				//Schiff ist vertikal.
				//Oberes Ende des Schiffs ermitteln:
				while(  zeile >= 0 && feld.getFieldstate(zeile, x) != FieldState.WATER) {
					zeile--;
				}//end while
				zeile++;
				int zeileTemp = zeile;
				//Groesse des Schiffs ermitteln:
				while(  zeileTemp < feld.size() && feld.getFieldstate(zeileTemp, spalte) == FieldState.SHIP) {
					zeileTemp++;
					shipSize++;
				}
				//vertikales Schiff löschen:
				for(int l = 0; l<shipSize; l++) {
					feld.setFieldstate(zeile + l, spalte, FieldState.WATER); 
				}//end for
				//Pruefen, ob in horizontaler Richtung platz ist:
				possible = this.spaceForShip(zeile, spalte, 'h', shipSize, feld, 'p', FieldState.SHIP); 
				if(possible) {
					//Falls Platz vorhanden, Schiff horizontal platzieren:
					for(int l = 0; l<shipSize; l++) {
						feld.setFieldstate(zeile, spalte + l, FieldState.SHIP); 
					}//end for
					return true;
				}
				//Falls kein Platz in horizontaler Richtung war, Schiff wieder vertikal Platzieren:
				for(int l = 0; l<shipSize; l++) {
					feld.setFieldstate(zeile + l, spalte, FieldState.SHIP);
				}//end for
			}else { //Schiff liegt horizontal.
				//linkes Ende des Schiffs ermitteln:
				while(  spalte >= 0 && feld.getFieldstate(zeile, spalte) != FieldState.WATER ) {
					spalte--;
				}//end while
				spalte++;
				int spalteTemp = spalte;
				//Groeße des Schiffs ermitteln:
				while(   spalteTemp < feld.size() &&  feld.getFieldstate(zeile, spalteTemp) == FieldState.SHIP) {
					spalteTemp++;
					shipSize++;
				}//end while
				//horizontales Schiff löschen:
				for(int l = 0; l<shipSize; l++) {
					feld.setFieldstate(zeile, spalte + l, FieldState.WATER);
				}//end for
				 //Pruefen, ob in vertikaler Richtung platz ist:
				possible = this.spaceForShip(zeile, spalte, 'v', shipSize, feld, 'p', FieldState.SHIP);
				if(possible) {
					//Falls Platz vorhanden, Schiff vertikal platzieren:
					for(int l = 0; l<shipSize; l++) {
						feld.setFieldstate(zeile + l, spalte, FieldState.SHIP);
					}//end for
					return true;
				}
				//Falls kein Platz in vertikaler Richtung war, Schiff wieder horizontal Platzieren:
				for(int l = 0; l<shipSize; l++) {
					feld.setFieldstate(zeile, spalte + l, FieldState.SHIP); 
				}//end for
			}
		}
		return false;
	}//end function
	
	/**
	 * Funktion um Schiffsspeicher zu fuellen.
	 * @param feld Das Feld, wo die Schiffe erstellt werden.
	 */
	public void createShips(Feld feld) {
		this.shipsLeft = feld.ships;
	}
	
	/**
	 * Funktion, die Überprüft, ob alle Schiffe einer bestimmten Schiffsklasse platziert wurden, bzw. sich kein Schiff der Klasse oder Groeße mehr im Speicher befindet.
	 * @param shipsize Die Groesse bzw. Klasse des Schiffs.
	 * @return True, falls alle Schiffe einer bestimmten Klasse bzw. Größe platziert wurden.
	 */
	public boolean shipsEmpty(int shipsize) {
		if(shipsize == 2) {
			if(this.shipsLeft[3] == 0) return true;
		} else if(shipsize == 3) {
			if(this.shipsLeft[2] == 0) return true;
		} else if(shipsize == 4) {
			if(this.shipsLeft[1] == 0) return true;
		} else if(shipsize == 5) {
			if(this.shipsLeft[0] == 0) return true;
		}
		return false;
		//Achtung: Wenn KI fuer den Spieler die Schiffe zufällig platziert, müssen alle Schiffe aus dem Speicher gelöscht werden.
	}

	/**
	 * Funltion, um zu ueberpruefen, ob alle Schiffe platziert wurden. 
	 * @return True, falls alle Schiffe platziert wurden, sonst false.
	 */
	public boolean allShipsPlaced() {
		for (int i = 0; i < shipsLeft.length; i++) {
			if (shipsLeft[i] != 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Funktion, die dem Schiffspeicher ein Schiff entnimmt, falls ein Schiff erfolgreich platziert wurde.
	 * @param shipsize Die Groesse des platzierten Schiffs, welches dann dem Speicher entnommen wird.
	 */
	public void reduceShip(int shipsize) {
		if(shipsize == 2) {
			this.shipsLeft[3]--;
		} else if(shipsize == 3) {
			this.shipsLeft[2]--;
		} else if(shipsize == 4) {
			this.shipsLeft[1]--;
		} else if(shipsize == 5) {
			this.shipsLeft[0]--;
		}
	}
	
	/**
	 * Funktion, die das Spielfeld reinigt, d.h alle Werte auf Wasser setzt und Schiffe entfernt.
	 * @param feld Das Feld, das gereinigt wird.
	 */
	public void clearField(Feld feld) {
		int n = feld.size();
		for(int y = 0; y<n; y++) {
			for(int x = 0; x<n; x++) {
				feld.setFieldstate(y, x, FieldState.WATER);
			}
		}
		feld.createShips();
		this.shipsLeft = feld.ships;
	}
	
	/**
	 * Getter für "versenkt" Variable.
	 * @return "versenkt" Variable.
	 */	
	public boolean getVersenkt() {
		return this.versenkt;
	}

	/**
	 * Speichert die Koordinaten, des zuletzt platzierten Schiffes.
	 * @param x Die Spalte im Feld.
	 * @param y Die Zeile im Feld.
	 * @param shipSize Größe vom Schiff.
	 */
	private void lastPlaced(int x, int y, int shipSize) {
			if (shipSize == 2) {
				last2[0] = x;
				last2[1] = y;
			} else if (shipSize == 3) {
				last3[0] = x;
				last3[1] = y;
			} else if (shipSize == 4) {
				last4[0] = x;
				last4[1] = y;
			} else if (shipSize == 5) {
				last5[0] = x;
				last5[1] = y;
			}
	}

	public int[] getLast2() {
			return last2;
	}
	public int[] getLast3() {
		return last3;
	}
	public int[] getLast4() {
		return last4;
	}
	public int[] getLast5() {
		return last5;
	}

	/**
	 * Setzt den Speicher für das zuletzt gesetzte Schiff zurueck.
	 */
	public void resetGetLast() {
			last2[0] = -1;
		last2[1] = -1;
		last3[0] = -1;
		last3[1] = -1;
		last4[0] = -1;
		last4[1] = -1;
		last5[0] = -1;
		last5[1] = -1;
	}
}
