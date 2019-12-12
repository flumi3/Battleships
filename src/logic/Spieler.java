package logic;

public abstract class Spieler {
	
	/**
	 * Array, das die x- und y- bzw. Spalten- und Zeilen-Koordinaten eines versenkten Schiffs speichert.
	 */
	private int[][] versenktesSchiff = new int[5][2]; 

    /**
     * Hilfsfunktion, die prueft, ob ein angeschossenes Schiff versenkt wurde und dies mitteilt.
     * Diese Funktion wird nach einem Schiffstreffer aufgerufen.
     * Bevor die Funktion aufgerufen wird, muss die angeschossene Position mit "Treffer Schiff" (= FieldState.SHIP_HIT) markiert werden.
     *
     * @param zieleld: Spielfeld, auf dem das Schiff getroffen wurde.
     * @param zeile: Zeilen-Koordinate des letzten Schusses.
     * @param spalte: Spalten-Koordinate des letzten Schusses.
     * @return Groesse des versenkten Schiffs, ansonsten 0.
     */
	public int versenktInfo(Feld zielfeld, int zeile, int spalte) {
		// initialisiere array mit -1
		for (int i = 0; i < versenktesSchiff.length; i++) {
			versenktesSchiff[i][0] = -1;
		}
		int i = 0;
		int shipSize = 0; //Z�hler der die Gr��e des Schiffs ermittelt, wenn es versenkt wurde. Wird, falls Versenkung stattfand, zur�ckgegeben.
		//Ausrichtung des angeschossenen Schiffs ermitteln. Abfragen, ob sich direkt �ber und unter der angschossenen Feldposition Wasser befindet.
		//Falls ja, muss die Ausrichtung des Schiffs horizontal sein. Ansonsten vertikal.
		if(    (  (zeile-1) == -1   ||   zielfeld.getFieldstate(zeile-1, spalte) == FieldState.WATER   ||   zielfeld.getFieldstate(zeile-1, spalte) == FieldState.WATER_HIT  )     &&     (  (zeile+1) == zielfeld.size() || zielfeld.getFieldstate(zeile+1, spalte) == FieldState.WATER || zielfeld.getFieldstate(zeile+1, spalte) == FieldState.WATER_HIT)) {
			//Schiff liegt horizontal. Zun�chst das linke Ende des Schiffs ermitteln.
			while(  (spalte >= 0 && zielfeld.getFieldstate(zeile, spalte) != FieldState.WATER )  && (spalte >= 0 && zielfeld.getFieldstate(zeile, spalte) != FieldState.WATER_HIT) ) {
				spalte--;
			}//end while
			spalte++;
			//Positionen des Schiffs von links nach rechts durchgehen bis Wasser (0 oder 2) oder das Feldende kommt.
			//Wenn auf dem Weg dorthin mindestens eine Position lediglich mit "Schiff"(=1) markiert ist, kann das Schiff noch nicht versenkt worden sein.
			while(   spalte < zielfeld.size() && (zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT || zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP)  ) {
				if(zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP) {
					return 0;
				}//end if
				versenktesSchiff[i][0] = zeile;
				versenktesSchiff[i][1] = spalte;
				i++;
				spalte++;
				shipSize++;
			}//end while
			return shipSize;
		//Else: Ausrichtung des Schiffs ist vertikal.
		}else {
			//Zun�chst oberes Ende des Schiffs ermitteln
			while(  (zeile >= 0 && zielfeld.getFieldstate(zeile, spalte) != FieldState.WATER )   &&  (zeile >= 0 && zielfeld.getFieldstate(zeile, spalte) != FieldState.WATER_HIT) ) {
				zeile--;
			}//end while
			zeile++;
			//Positionen des Schiffs von oben nach unten durchgehen bis Wasser (0 oder 2) oder das Feldende kommt.
			//Wenn auf dem Weg dorthin nur eine Position lediglich mit "Schiff" (=1) markiert ist, kann das Schiff noch nicht versenkt worden sein.
			while(  zeile < zielfeld.size() && (zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT || zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP)  ) {
				if(zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP) {
					return 0;
				}
				versenktesSchiff[i][0] = zeile;
				versenktesSchiff[i][1] = spalte;
				i++;
				zeile++;
				shipSize++;
			}
			return shipSize;
		}
	}
	
    /**
     * Hilfsmethode, die Ueberprueft, ob - ausgehend von der ausgewaehlten Position - ein Schiff Platz hat. Funktion wird zum Platziern von Schiffen verwendet 
     * (In der Klasse RealPlayer innerhalb der Funktion placeShip(...); In der Klasse KIGegner innerhalb der Funktionen placeShips(...) sowie shoot(...))
     *
     * @param zeile: Zeilen-Koordinate des letzten Schusses.
     * @param spalte: Spalten-Koordinate des letzten Schusses.
     * @param feld: Spielfeld, auf dem geprueft werden soll.
     * @param ausrichtung: gibt an, in welche Richtung geprueft wird. Dabei sind nur zwei Werte erlaubt: 'v' fuer vertikale Richtung und 'h' fuer horizontale Richtung.
     * @param shipSize: gibt an fuer welchen Schiffgroesse geprueft werden soll.
     * @param parent: gibt an, welches die Elternfunktion ist, d.h. 'p' feur placeShips(...) und 's' fuer shoot(...).
     * @param state: gibt an, gegen welchen FieldState geprueft werden soll (SHIP, WATER_HIT, SHIP_HIT).
     * 
     * @return true, wenn Platz f�r ein Schiff vorhanden ist, sonst false.
     */
	protected boolean spaceForShip(int zeile, int spalte, char ausrichtung, int shipSize, Feld feld, char parent, FieldState state) {
		if(zeile < 0 || zeile >= feld.size() || spalte < 0 || spalte >= feld.size()) return false;
		FieldState edge = null;
		FieldState center = null;
		if( parent == 'p') {
			edge = FieldState.SHIP;
			center = FieldState.SHIP;
		}else if( parent == 's' && state == FieldState.SHIP_HIT) {
			edge = FieldState.SHIP_HIT;
			center = FieldState.SHIP_HIT;
		}else if( parent == 's' && state == FieldState.WATER_HIT) {
			edge = FieldState.SHIP_HIT;
			center = FieldState.WATER_HIT;
		}
		if( ausrichtung == 'h' ) {
			for(int j = 0; j < shipSize; j ++) {
				if( (spalte + j) >= feld.size()) {
					return false;
				}//end if
				boolean positionBelegt = ( feld.getFieldstate(zeile, spalte + j) == center );
				boolean positionDarueberDarunterBelegt = (  ( (zeile-1) != -1 && feld.getFieldstate(zeile-1, spalte + j) == edge ) || ( (zeile+1) != feld.size() && feld.getFieldstate(zeile+1, spalte + j) == edge ));
				boolean positionRechtsBelegt = ( ((spalte+j+1) < feld.size() && feld.getFieldstate(zeile, spalte + j + 1) == edge) );
				boolean positionenDiagonalRechtsBelegt = ( ( (zeile-1) > -1 && (spalte + j + 1) < feld.size() && feld.getFieldstate(zeile-1, spalte + j + 1) == edge ) || ( (zeile+1) < feld.size() && spalte + j + 1 < feld.size() && feld.getFieldstate(zeile+1, spalte + j + 1) == edge  ));
				boolean positionGanzLinksBelegt = false;//Kann nur zum ersten Schleifendurchlauf ermittelt werden, siehe nächste If-Anweisung
				boolean positionenDiagonalGanzLinksBelegt = false;
				if( j == 0) {
					positionGanzLinksBelegt = (  ((spalte-1) > -1 && feld.getFieldstate(zeile, spalte - 1) == edge ) );
					positionenDiagonalGanzLinksBelegt = ( (zeile-1) > -1 && spalte -1 > -1 && feld.getFieldstate(zeile-1, spalte -1 ) == edge ) || ( (zeile+1) < feld.size() && spalte - 1 > -1 && feld.getFieldstate(zeile+1, spalte - 1) == edge  );
				}
				if( positionBelegt  || positionDarueberDarunterBelegt || positionRechtsBelegt || positionenDiagonalRechtsBelegt || positionGanzLinksBelegt || positionenDiagonalGanzLinksBelegt ) {
					return false;
				}//end if
			}//end for
		}
		if( ausrichtung == 'v' ) {
			for(int j = 0; j < shipSize; j ++) {
				if( (zeile + j) >= feld.size()) {
					return false;
				}//end if
				boolean positionBelegt = ( feld.getFieldstate(zeile + j, spalte) == center  );
				boolean positionLinksOderRechtsBelegt = (  ( (spalte-1) != -1 && feld.getFieldstate(zeile + j, spalte-1) == edge  ) || ( (spalte+1) != feld.size() && feld.getFieldstate(zeile + j, spalte + 1) == edge  ));
				boolean positionDarunterBelegt = ( ((zeile+j+1) < feld.size() && feld.getFieldstate(zeile + j + 1, spalte) == edge ) );
				boolean positionenDiagonalDarunterBelegt = ( (spalte-1) > -1 && zeile + j + 1 < feld.size() && feld.getFieldstate(zeile + j + 1, spalte-1) == edge  ) || ( (spalte+1) < feld.size() && zeile + j + 1 < feld.size() && feld.getFieldstate(zeile + j + 1, spalte + 1) == edge  );
				boolean positionDarueberBelegt = false;//Kann nur zum ersten Schleifendurchlauf ermittelt werden, siehe nächste If-Anweisung
				boolean positionenDiagonalDarueberBelegt = false;
				if( j == 0) {
					positionDarueberBelegt = ( (zeile-1) > -1 && feld.getFieldstate(zeile - 1, spalte) == edge );
					positionenDiagonalDarueberBelegt = ( (spalte-1) > -1 && (zeile-1) > -1 && feld.getFieldstate(zeile-1, spalte-1) == edge  ) || ( (spalte+1) < feld.size() && zeile - 1 > -1 && feld.getFieldstate(zeile - 1, spalte + 1) == edge  );
				}
				if( positionBelegt  || positionLinksOderRechtsBelegt || positionDarunterBelegt || positionenDiagonalDarunterBelegt || positionDarueberBelegt || positionenDiagonalDarueberBelegt) {
					return false;
				}//end if
			}//end for
		}
		return true;
	}//end function
	
	/**
	 * Getter fuer ein Array, das die Koordinaten eines versenkten Schiffs speichert.
	 */
	public int[][] getVersenktesSchiff() {
		return versenktesSchiff;
	}
}
