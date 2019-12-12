package logic;

public enum FieldState {
	WATER, SHIP, WATER_HIT, SHIP_HIT;
	
	//Nur zu Testzwecken. Durch das �berschreiben der toString-Methode wird, falls das Feld auf der Konsole gedumpt wird, der Feldinhalt mit den Enum-Int-Werten angezeigt.
	public String toString(){
		return "" + this.compareTo(FieldState.WATER);
	}
}
