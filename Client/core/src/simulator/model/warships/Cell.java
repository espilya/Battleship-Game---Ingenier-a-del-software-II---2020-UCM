package simulator.model.warships;

import java.io.Serializable;

/**
 * Base of the game. Used as building blocks to build up the game more
 * concretely the board and objects contained by it <br>
 * Representation: ~ = water, X = Killed cell, O = Missed shot, P1 = PatrolBoat
 * with id == 1,...
 */
public class Cell implements Serializable {
	private static final long serialVersionUID = -856310608677571344L;

	public static final String WATER_CELL = "~";
	public static final String KILLED_CELL = "X";
	public static final String HITTED_CELL = "O";
	public static final String MISSED_CELL = " ";

	/**
	 * ~ = water, X = Killed cell, O = Missed shot, P1 = PatrolBoat with id == 1
	 */
	private String cell;
	private int x, y;

	private boolean shield;

	/**
	 * Constructor for the Cell class
	 * 
	 * @param x
	 * @param y
	 * @param str
	 */
	public Cell(int x, int y, String str) {
		this.x = x;
		this.y = y;
		cell = str;
		shield = false;
	}

	/*
	 * Overwrite of the equals method and compares the content of the cell with a
	 * string
	 * 
	 * @param str
	 * 
	 * @return TRUE or FALSE
	 */
	public boolean equals(String str) {
		return (cell.equals(str) ? true : false);
	}

	public String get() {
		return cell;
	}

	public void set(String cell) {
		this.cell = cell;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int[] getPos() {
		return new int[] { x, y };
	}

	public void setPos(int[] pos) {
		x = pos[0];
		y = pos[1];
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {

	}

	@Override
	public String toString() {
		return cell;
	}

	public boolean isShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
	}
}
