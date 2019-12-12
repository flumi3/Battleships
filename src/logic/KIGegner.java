package logic;

/**
 * Klasse, die eine KI repraesentiert, die selbststaendig in der Lage ist, Schiffe auf einem Feld zu platzieren 
 * und vernuenftige Entscheidungen bezueglich ihrer Schussversuche treffen kann.
 *
 */
public class KIGegner extends Spieler {
	
	/**
	 * Zaehler, der Treffer waehrend eines Spielzugs bzw. innerhalb der Funktion shoot() zaehlt.
	 */
	private int hitCount = 0;
	
	/**
	 * Zeilen-Koordinate, die von der KI für einen Schuss gewaehlt wurde.
	 */
	public int zeile;
	
	/**
	 * Spalten-Koordinate, die von der KI für einen Schuss gewaehlt wurde.
	 */
	public int spalte;
	
	/**
	 * Zeilen-Koordinate, des letzten Schiffstreffers.
	 */
	int letzte_zeile = -1;
	
	/**
	 * Spalten-Koordinate, des letzten Schiffstreffers.
	 */
	int letzte_spalte = -1;
	
	/**
	 * Zeilen-Koordinate, des vorletzten Schiffstreffers.
	 */
	int vorletzte_zeile = -1;
	
	/**
	 * Spalten-Koordinate, des vorletzten Schiffstreffers.
	 */
	int vorletzte_spalte = -1;
	
	/**
	 * Speicher der innerhalb der Funktion shoot(...) verwendet wird und angibt welche Schiffe noch nicht versenkt wurden. 
	 * Wird mit Werten initialisiert innerhalb der Funktion placeShips(...).
	 */
	int shipsLeft[] = new int[4];
	
	/**
	 * X-Koordinate bzw. Spalte-Koordinate, auf die zuletzt geschossen wurde 
	 */
	private int shotX;
	
	/**
	 * Y-Koordinate bzw. Zeilen-Koordinate, auf die zuletzt geschossen wurde 
	 */
	private int shotY;

    /**
     * KI-Funktion zum Platzieren aller für ein Spiel verfügbaren Schiffen. Die Schiff werden dabei zufällig platziert.
     * Schafft die KI es nicht die Schiffe aus Platzgruenden zu platzieren, faengt sie innerhalb einer whie-Schleife von Vorne an.
     *
     * @param eigenesFeld: Spielfeld, auf dem die Schiffe platziert werden.
     * @return void.
     */
	public void placeShips(Feld eigenesFeld) {
		int[] schiffspeicher = new int[4];
		for(int i = 0; i < 4; i++) {
			schiffspeicher[i] = eigenesFeld.getShips(i);
			this.shipsLeft[i] = eigenesFeld.getShips(i); //Schiffsspeicher initialisieren. Array wird in dieser Funktion (placeShips()) nicht weiter verwendet sondersn erst wieder in der Funktion shoot().
		}//end for
		//Auï¿½ere For-schleife geht schiffsspeicher-Array durch, in dem die Anzahl der jeweiligen Schiffsklassen gespeichert sind. 
		//Insesamt gibt es 4 Schiffsklassen, von daher endet der Zï¿½hler i der Schleife bei 3
		for(int i = 0; i < 4; i++) {
			int shipSize = 0;
			//mit Switch-Case Schiffsgrï¿½ï¿½e anhand der Index-Postion im Array "ships" der Klasse Feld ermitteln
			switch(i) {
				case 0: 
					shipSize = 5;
					break;
				case 1: 
					shipSize = 4;
					break;
				case 2: 
					shipSize = 3;
					break;
				case 3: 
					shipSize = 2;
					break;
			}//end for
			//Innere Schleife entnimmt solange ein Schiff des in Index i des schiffsspeicher gespeicherten Schiffe bis von der Schiffsklasse kein Schiff mehr vorhanden ist und platziert es zufï¿½llig auf dem Feld.
			while(schiffspeicher[i] != 0) {
				int zeile = -1;
				int spalte = -1;
				int ausrichtung = -1;
				int zaehler = 0;
				int[][] bereitsVersuchtSpeicher = new int[eigenesFeld.size()][eigenesFeld.size()]; //Hilfsspeicher, der die Platzierungsversuche fï¿½r ein Schiff speichert
				boolean possible = true; //Variable, die im Folgenden angibt, ob ein Platzierungsversuch erfolgreich war (true) oder nicht (false).
						do {
							//Spalten- und Zeilenindex sowie Ausrichtung des Schiffs zufï¿½llig ermitteln.
							zeile = (int) (Math.random() * eigenesFeld.size());
							spalte = (int) (Math.random() * eigenesFeld.size());
							ausrichtung = (int) (Math.random() * 2);//werte fï¿½r ausrichtung: 0 = horizontal, 1 = vertikal
							
							//Wenn auf ermittelter Position auf dem eigenen Feld (eigenesFeld) nicht bereits ein erfolgloser Platzierungsveruch stattfand, wird ï¿½berprï¿½ft, ob das Schiff platziert werden kann.
							if(bereitsVersuchtSpeicher[zeile][spalte] != 1) {
								//Zufallsgenerator hat entschieden, dass zuerst versucht wird, das Schiff horzontal zu platzieren
								if(ausrichtung == 0) {
									//1. Mï¿½glichkeit: ï¿½berprï¿½fen, ob das Schiff horizontal, von links nach rechts platziert werden kann
									possible = this.spaceForShip(zeile, spalte, 'h', shipSize, eigenesFeld, 'p', FieldState.SHIP);
									//2. Mï¿½glichkeit: Falls 1. Mï¿½glichkeit nicht realisiert werden kann, wird ï¿½berprï¿½ft, ob das Schiff vertikal, von oben nach nach unten platziert werden kann.
									if( !possible ) {
										ausrichtung = 1;
										possible = this.spaceForShip(zeile, spalte, 'v', shipSize, eigenesFeld, 'p', FieldState.SHIP);
									}//end if
								}else{//Zufallsgenerator hat entschieden, dass zuerst versucht wird, das Schiff horzontal zu platzieren
									//1. Mï¿½glichkeit: Es wird ï¿½berprï¿½ft, ob das Schiff vertikal, von oben nach unten platziert werden kann
									possible = this.spaceForShip(zeile, spalte, 'v', shipSize, eigenesFeld, 'p', FieldState.SHIP);
									//2. Mï¿½glichkeit: Fals 1. Mï¿½glichkeit nicht realisiert werden kann, wird ï¿½berprï¿½ft ob das Schiff horizontal, von links nach rechts platziert werden kann
									if( !possible ) {
										ausrichtung = 0;
										possible = this.spaceForShip(zeile, spalte, 'h', shipSize, eigenesFeld, 'p', FieldState.SHIP);
									}//end if
								}//end else
							bereitsVersuchtSpeicher[zeile][spalte] = 1; //Platzierungsversuch wird im Hilfsarray gespeichert
							zaehler = 0;
							//Ineinanderverschachtelte For-Schleife zï¿½hlt die Platzierungsversuche fï¿½r ein Schiff. Wenn auf allen Positionen ein erfolgloser Platzierungsversuch stattfand, kann das Schiff nicht mehr platziert werden. Dann wird die Funktion placeShips von Neuem Aufgerufen 
							for(int k = 0; k < eigenesFeld.size(); k++) {
								for(int l = 0; l < eigenesFeld.size(); l++) {
									if(bereitsVersuchtSpeicher[k][l] == 1) {
										zaehler++;
									}//end if
								}//end for
							}//end for
							if( zaehler == eigenesFeld.size() * eigenesFeld.size() ) {
								//Wenn erfolglos versucht wurde, ein Schiff auf allen Positionen zu Platzieren ist die komplette Platzierung aller Schiffe gescheitert. 
								//Das Feld auf dem die Schiffe platziert werden wird dann gereinigt, d.h. wird mit 0en gefï¿½llt, und die Funktion placeShips() wird erneut aufgerufen.
								for(int z = 0; z < eigenesFeld.size(); z++) {
									for(int s = 0; s < eigenesFeld.size(); s++) {
										eigenesFeld.setFieldstate(z, s, FieldState.WATER);
									}//end for
								}//end for
								this.placeShips(eigenesFeld);//Rekursiver Aufruf der Funktion bei gescheiterter Platzierung der Schiffe
								return;
							}//end if
						}//end if
					}while( !(possible) );
						
					//Wurde die Do-While-Schleife verlassen kann das Schiff platziert werden. In Switch-Case_Anweisung werden die entsprechenden Werte ins Feld eingetragen
					switch(ausrichtung) {
						case 0: //Das Schiff wird von der ermittelten Position aus horizontal platziert
							for(int l = 0; l<shipSize; l++) {
								eigenesFeld.setFieldstate(zeile, spalte + l, FieldState.SHIP);
							}//end for
							break;
						case 1://Das Schiff wird von der ermittelten Position aus mit der Ausrichtung vertikal platziert
							for(int l = 0; l<shipSize; l++) {
								eigenesFeld.setFieldstate(zeile + l, spalte, FieldState.SHIP);
							}//end for
							break;
					}//end switch
					schiffspeicher[i]--;//Das Schiff wurde erfolgreich platziert und im Array schiffsspeicher wird von der platzierten Schiffsklasse ein Schiff reduziert		
			}//end while	
		}//end for
	}//end function
	
    /**
     * KI-Funktion, die auf einem Feld eine Feldposition waehlt, auf die geschossen werden soll.
     * Die Funktion unterscheidet dabei vier Möglichkeiten:
     * 	1. Moeglichkeit: es existiert zur Zeit kein angeschossenes und nicht versenktes Schiff.
     * 	2. Moeglichkeit: Schiff wurde bisher erst einmal getroffen, d.h. die Ausrichtung (horizontal oder vertikal) 
     * 	   des Schiffs ist noch nicht bekannt.
     * 	3. Moeglichkeit: Schiff wurde bisher mehr als einmal Mal getroffen und Ausrichtung des Schiffs ist horizontal.
     * 	4. Moeglichkeit: Schiff wurde bisher mehr als einmal Mal getroffen und Ausrichtung des Schiffs ist vertikal.
     *
     * @param zielfeld: Spielfeld, das geschossen werden soll.
     * @return void.
     */
	public void shoot(Feld zielfeld) {
		//1. Mï¿½glichkeit: es existiert zur Zeit kein angeschossenes und nicht versenktes Schiff.
		if(hitCount == 0) {
			//Hilfsfunktion fï¿½r die 1. Mï¿½glichkeit.
			noHitInThePast(zielfeld);
			shotX = spalte;
			shotY = zeile;
			System.out.println("Schuss auf Stelle (" + zeile + ", " + spalte + ")");
			return;
		}//end if
		//2. Mï¿½glichkeit: Schiff wurde bisher erst einmal getroffen, d.h. die Ausrichtung (horizontal oder vertikal) des Schiffs ist noch nicht bekannt.
		if(hitCount == 1) {
			shipHitOnce(zielfeld);
			shotX = spalte;
			shotY = zeile;
			System.out.println("Schuss auf Stelle (" + zeile + ", " + spalte + ")");//Nur zu Testzwecken.
			return;
		}//end if
		//3. Mï¿½glichkeit: Schiff wurde bisher mehr als einmal Mal getroffen und Ausrichtung des Schiffs ist horizontal.
		if(hitCount > 1 && letzte_zeile == vorletzte_zeile) {
			this.shipIsHorizontal(zielfeld);
			shotX = spalte;
			shotY = zeile;
			System.out.println("Schuss auf Stelle (" + zeile + ", " + spalte + ")");//Nur zu Testzwecken.
			return;
		}//end if
		//4. Mï¿½glichkeit: Schiff wurde bisher mehr als einmal Mal getroffen und Ausrichtung des Schiffs ist vertikal.
		if(hitCount > 1 && vorletzte_spalte == letzte_spalte) {
			this.shipIsvertical(zielfeld);
			shotX = spalte;
			shotY = zeile;
			System.out.println("Schuss auf Stelle (" + zeile + ", " + spalte + ")");//Nur zu Testzwecken.
			return;
		}
	}//end function
	
    /**
     * Funktion, die den Feldstatus auf dem beschossenen Feld nach einem Schuss veraendert. 
     * Wurde Wasser getroffen, wird die Stelle auf dem Feld mit FieldState.WATER_HIT markiert. 
     * Wurde ein Schiff getroffen, wird die Stelle mit FieldState.SHIP_HIT markiert.
     *
     * @param zielfeld: Spielfeld, das geschossen wurde.
     * @param fieldstate: Ursprünglicher Feldstatus der beschossenen Feldposition (FieldState.WATER oder FieldState.SHIP).
     * @return true, wenn ein Schiff getroffen wurde; false, wenn Wasser getroffen wurde.
     */
	public boolean schussVerarbeiten(Feld zielfeld, FieldState fieldstate) {
		if(fieldstate == FieldState.WATER) {
			zielfeld.setFieldstate(zeile, spalte, FieldState.WATER_HIT); //Im Falle eines Treffer ins Wasser wird die Stelle im Feld mit Treffer Wasser (2) markiert und Funktion mit false beendet.
			System.out.println("Kein Treffer!");//TEST
			return false;
		} else if(fieldstate == FieldState.SHIP) {
			vorletzte_zeile = letzte_zeile;
			vorletzte_spalte = letzte_spalte;
			letzte_zeile = zeile;
			letzte_spalte = spalte;
			hitCount += 1;
			zielfeld.setFieldstate(zeile, spalte, FieldState.SHIP_HIT);
			System.out.println("Treffer!");//TEST
			zielfeld.decrementCounter();
			return true;
		}
		return false; //Wird nicht erreicht.
	}
	
    /**
     * Zusaetzlich zur Fukntionsweise der vererbten Methode versenktInfo aus der Eltern-Klasse "Spieler", 
     * werden hier im Falle einer Versenkung für die KI notwendige Attribute zurueckgesetzt.
     *
     * @param zielfeld: Spielfeld, das geschossen wurde.
     * @param zeile: Zeilen-Koordinate, auf die geschossen wurde.
     * @param spalte: Spalten-Koordinate, auf die geschossen wurde.
     * 
     * @return gibt die Groesse des versenkten Schiffes zurück, falls es versenkt wurde, ansonsten 0.
     */
	@Override
	public int versenktInfo(Feld zielfeld, int zeile, int spalte) {
		int versenkt = super.versenktInfo(zielfeld, zeile, spalte);
		if(versenkt != 0) {
			if(zielfeld.getFieldstate(zeile, spalte) == FieldState.WATER_HIT) {
				System.out.println("EY! Zeile 189");
			}
			System.out.println("Versenkt!");//TEST
			letzte_zeile = -1;
			letzte_spalte = -1;
			vorletzte_zeile = -1;
			vorletzte_spalte = -1;
			hitCount = 0;
			int shipSize = versenkt;
			if(shipSize == 2) {
				shipsLeft[3]--;
			}else if(shipSize == 3) {
				shipsLeft[2]--;
			}else if(shipSize == 4) {
				shipsLeft[1]--;
			}else if(shipSize == 5) {
				shipsLeft[0]--;
			}
		}
		return versenkt;
	}
	
	
    /**
     * Hilfsfunktion zum Auswaehlen einer Position, auf die geschossen werden soll, 
     * wenn zur Zeit kein angeschossenes, nicht versenktes Schiff existiert.
     * Wird innerhalb der Methode shoot(...) aufgerufen.
     *
     * @param zielfeld: Spielfeld, auf das geschossen wurde.
     * @return void
     */
	private void noHitInThePast(Feld zielfeld) {
		boolean makesSense = true; //Variable possible prï¿½ft, ob ein Schuss auf die ermittelte Position Sinn macht. D.h ob das Feld auf das geschossen werden soll, bereits beschossen wurde, ob im Rahmen um das Feld bereits ein getroffenes Schiff ist oder ob von der ermittelten Position aus kein Platz fï¿½r das kleinste noch nicht versenkte Schiff wï¿½re.
		int counter = 0;
		int zähler = 0;
		//Zufï¿½llige Position im Feld, auf die geschossen werden soll wird ermittelt:
		do {
			makesSense = true;
			boolean noHitWaterCrossing;
			boolean noHitShipCrossing;
			counter = 0;;
			//Position,auf die geschossen werden soll wird zufï¿½llig ermittelt.
			zeile = (int) (Math.random() * zielfeld.size()); //Zahl zwischen 0 und Feld-Seitenlï¿½nge minus 1 wird fï¿½r die Zeile ermittelt.
			spalte = (int) (Math.random() * zielfeld.size());//Zahl zwischen 0 und Feld-Seitenlï¿½nge minus 1 wird fï¿½r die Zeile ermittelt.
			//Prï¿½fen, ob Schuss auf ermittelte Position Sinn machen wï¿½rde. D.h. es wird geprï¿½ft, ob Platz fï¿½r ein Schiff vorhanden wï¿½re bzw. ob die Position bereits beschossen wurde.
			int smallestShip = this.smallestShipLeft(zielfeld);//Kleinstes nicht versenktes Schiff wird ermittelt, um mï¿½gliche Abstï¿½nde zu definieren.
			//Zuerst wird geprï¿½ft, ob in ein Schuss in horizontaler Richtung von der ermittelten Position Sinn macht.
			for(int i = spalte; i > spalte - smallestShip; i--) {
				noHitShipCrossing = this.spaceForShip(zeile, spalte, 'h', smallestShip, zielfeld, 's', FieldState.SHIP_HIT);
				noHitWaterCrossing = this.spaceForShip(zeile, spalte, 'h', smallestShip, zielfeld, 's', FieldState.WATER_HIT);
				if(!noHitShipCrossing || !noHitWaterCrossing) {
					counter++;//Anzahl der ausgeschlossenen Mï¿½glichkeiten zï¿½hlen.
				}
			}
			////Als nï¿½chstes wird geprï¿½ft, ob in ein Schuss in vertikaler Richtung von der ermittelten Position Sinn macht.
			for(int i = zeile; i > zeile - smallestShip; i--) {
				noHitShipCrossing = this.spaceForShip(zeile, spalte, 'v', smallestShip, zielfeld, 's', FieldState.SHIP_HIT);
				noHitWaterCrossing = this.spaceForShip(zeile, spalte, 'v', smallestShip, zielfeld, 's', FieldState.WATER_HIT);
				if(!noHitShipCrossing || !noHitWaterCrossing) {
					counter++;//Anzahl der ausgeschlossenen Mï¿½glichkeiten zï¿½hlen.
				}
			}
			if( counter == smallestShip * 2) { //Die Anzahl der Mï¿½glichkeiten, dass ein Schiff auf der ermittelten Position existiert die Grï¿½ï¿½e des kleinsten nicht versenkten Schiff multipliziert mit zwei. Wurde diese Anzahl von Mï¿½glichkeiten augeschlossen, macht ein Schussversuch keinen Sinn.
				makesSense = false;
			}
			zähler++;
			if(zähler == 100000) {
				System.out.println("Uups! Zeile 245");
			}
		}while(!makesSense); //Schleife wird erst verlassen, wenn im Feld eine Position gefunden wurde, auf die ein Schussversuch Sinn macht.
	}
	
    /**
     * Hilfsfunktion zum Auswaehlen einer Position, auf die geschossen werden soll, 
     * wenn ein Schiff bisher einmal getroffen wurde. D.h. die Ausrichtung des Schiffs ist nicht bekannt.
     * Wird innerhalb der Methode shoot(...) aufgerufen.
     *
     * @param zielfeld: Spielfeld, auf das geschossen wurde.
     * @return void
     */
	private void shipHitOnce(Feld zielfeld) {
		boolean belegt = false;
		int zähler = 0;
		do {
			int zufallszahl = (int) (Math.random() * 4);
			zeile = letzte_zeile;
			spalte = letzte_spalte;
			switch(zufallszahl) { //Der Zufall entscheidet in welche Richtung weitergeschossen wird.
			case 0:
				zeile = letzte_zeile + 1;
				break;
			case 1:
				zeile = letzte_zeile - 1;
				break;
			case 2:
				spalte = letzte_spalte + 1;
				break;
			case 3:
				spalte = letzte_spalte - 1;
				break;
			}//end switch
			belegt = false;
			//Pruefen, ob ermittelte Feldpositiont bereits beschossen wurde oder sich auserhalb des Spielfeldes befindet
			if(zeile >= zielfeld.size() || spalte >= zielfeld.size() || zeile < 0 || spalte < 0 || zielfeld.getFieldstate(zeile, spalte) == FieldState.WATER_HIT || zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
				belegt = true;
			} else if(belegt == false){
				//Ermitteln, ob im Rahmen, um die ermittelte Position bereits ein Schiff getroffen wurde, wobei die letzte getroffene Position davon ausgenommen wird.
				for(int i = zeile-1; i <= zeile+1; i++) {
					for(int j = spalte-1; j <= spalte+1; j++) {
						if( i != letzte_zeile && j != letzte_spalte) {
							if( i >= 0 && i < zielfeld.size() && j >= 0 && j < zielfeld.size() && zielfeld.getFieldstate(i, j) == FieldState.SHIP_HIT) {
								belegt = true;
								break;
							}
						}
					}
				}
			}
			zähler++;
			if(zähler == 100000) {
				System.out.println("Uups! Zeile 293");
			}
		}while(belegt);
	}//end function
	
    /**
     * Hilfsfunktion, die innerhalb von KIGegner.shoot() aufgerufen wird, falls ein Schiff in unmittelbarer Vergangenheit getroffen 
     * und nicht versenkt wurde und sicher ist, dass das Schiff horizontal liegt.
     * Wird innerhalb der Methode shoot(...) aufgerufen.
     *
     * @param zielfeld: Spielfeld, auf das geschossen wurde.
     * @return void
     */
	private void shipIsHorizontal(Feld zielfeld) {
		boolean belegt = false;
		int zähler= 0;
		do {
			spalte = letzte_spalte;
			zeile = letzte_zeile;
			int zufallszahl = (int) (Math.random() * 2); //Der Zufall entscheidet in welche Richtung (rechts oder links) weitergeschossen wird.
			if(zufallszahl == 0) { //es wird links weitergeschossen
				while(zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
					spalte--;
					if(spalte < 0) break;
				}
			} else { //es wird rechts (zufallszahl = 1) weitergeschossen
				while(zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
					spalte++;
					if(spalte >= zielfeld.size()) break;
				}
			}
			belegt = false;
			//Prï¿½fen, ob ermittelte Feldpositiont bereits beschossen wurde oder sich auserhalb des Spielfeldes befindet
			if(zeile >= zielfeld.size() || spalte >= zielfeld.size() || zeile < 0 || spalte < 0 || zielfeld.getFieldstate(zeile, spalte) == FieldState.WATER_HIT || zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
				belegt = true;
			} else {
				//Ermitteln, ob im Rahmen, um die ermittelte Position bereits ein Schiff getroffen wurde, wobei die letzte getroffene Position davon ausgenommen wird.
				for(int i = zeile-1; i <= zeile+1; i++) {
					for(int j = spalte-1; j <= spalte+1; j++) {
						if( (i != letzte_zeile && j != letzte_spalte) || (i != vorletzte_zeile && j != vorletzte_spalte)) {
							if( i >= 0 && i < zielfeld.size() && j >= 0 && j < zielfeld.size() && zielfeld.getFieldstate(i, j) == FieldState.SHIP_HIT) {
								belegt = true;
								break;
							}
						}
					}
				}
			}
			zähler++;
			if(zähler == 100000) {
				System.out.println("Uups! Zeile 338");
			}
		}while(belegt);
	}
	
    /**
     * Hilfsfunktion, die innerhalb von KIGegner.shoot() aufgerufen wird, falls ein Schiff in unmittelbarer Vergangenheit getroffen 
     * und nicht versenkt wurde und sicher ist, dass das Schiff vertikal liegt.
     *
     * @param zielfeld: Spielfeld, auf das geschossen wurde.
     * @return void.
     */
	private void shipIsvertical(Feld zielfeld) {
		boolean belegt = false;
		int zähler = 0;
		do {
			spalte = letzte_spalte;
			zeile = letzte_zeile;
			int zufallszahl = (int) (Math.random() * 2); //Der Zufall entscheidet in welche Richtung (oben oder unten) weitergeschossen wird.
			
			if(zufallszahl == 0) { //es wird oben weitergeschossen
				while(zeile >=0 && zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
					zeile--;
					if(zeile < 0) break;
				}
			} else { //es wird unten (zufallszahl = 1) weitergeschossen
				while(zeile < zielfeld.size() && zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
					zeile++;
					if(zeile >= zielfeld.size()) break;
				}
			}
			belegt = false;
			//Pruefen, ob ermittelte Feldpositiont bereits beschossen wurde oder sich auserhalb des Spielfeldes befindet
			if(zeile >= zielfeld.size() || spalte >= zielfeld.size() || zeile < 0 || spalte < 0 || zielfeld.getFieldstate(zeile, spalte) == FieldState.WATER_HIT || zielfeld.getFieldstate(zeile, spalte) == FieldState.SHIP_HIT) {
				belegt = true;
			}
			//Ermitteln, ob im Rahmen, um die ermittelte Position bereits ein Schiff getroffen wurde, wobei die letzte getroffene Position davon ausgenommen wird.
			for(int i = zeile-1; i <= zeile+1; i++) {
				for(int j = spalte-1; j <= spalte+1; j++) {
					if( (i != letzte_zeile && j != letzte_spalte) || (i != vorletzte_zeile && j != vorletzte_spalte)) {
						if( i >= 0 && i < zielfeld.size() && j >= 0 && j < zielfeld.size() && zielfeld.getFieldstate(i, j) == FieldState.SHIP_HIT) {
							belegt = true;
							break;
						}
					}
				}
			}
			zähler++;
			if(zähler == 100000) {
				System.out.println("Uups! Zeile 380"
						+ "");
				zielfeld.printField();
			}
		}while(belegt);
	}//end function
	
	
    /**
     * Die Funktion ermittelt das kleinste noch nicht versenkte Schiff auf dem gegnerischen Feld.
     * Wird innerhalb der Methode noHitInThePast(...) aufgerufen.
     *
     * @param zielfeld: Spielfeld, auf dem das kleinste noch nicht versenkte Schiff ermittelt werden soll.
     * @return Groesse des kleinsten noch nicht versenkten Schiffs.
     */
	private int smallestShipLeft(Feld zielfeld) {
		int index = 0;
		for(int i = shipsLeft.length-1; i >= 0; i--) {
			if(shipsLeft[i] != 0) {
				index = i;
				break;
			}
		}
		if(index == 0) {
			return 5;
		}else if(index == 1) {
			return 4;
		}else if(index == 2) {
			return 3;
		}
		return 2;
	}
	
	

    /**
     * Getter für die X-(Spalten-)Koordinate des letzten Schuss.
     *
     * @param -
     * @return die X-(Spalten-)Koordinate des letzten Schuss.
     */
	public int getShotX() {
		return shotX;
	}

	/**
     * Getter für die Y-(Zeilen-)Koordinate des letzten Schuss.
     *
     * @param -
     * @return die X-(Zeilen-)Koordinate des letzten Schuss.
     */
	public int getShotY() {
		return shotY;
	}
	
}
 