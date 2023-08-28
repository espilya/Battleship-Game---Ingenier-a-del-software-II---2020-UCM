package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import simulator.model.GameSize;

public class GenerationControl implements Serializable {
	private static final long serialVersionUID = 8472519130112861078L;

	private List<Pair<Integer, Integer>> possibles = new ArrayList<>();
	int boardSize;

	// Constructor for the GenerationControl Class. It makes it so that all possible
	// attack positions are included in the List. The size of this will change in
	// accordance to the GameType chosen
	/**
	 * 
	 * @param tam
	 */
	public GenerationControl(GameSize tam) {
		boardSize = tam.getSize();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				getPossibles().add(new Pair<Integer, Integer>(i, j));
			}
		}
	}

	// Removes a given position from the possible positions in order to ensure that
	// the same position cannot be attacked twice
	/**
	 * 
	 * @param pos
	 */
	public void removeFromPossibles(Pair<Integer, Integer> pos) {
		getPossibles().remove(pos);
	}

	// Will return true if it is possible to attack that position otherwise it will
	// return false
	public boolean isNotInPossibles(Pair<Integer, Integer> pos) {
		boolean isNotInPossibles = false;
		for (int i = 0; i < getPossibles().size(); i++) {
			if (getPossibles().get(i).equals(pos)) {
				isNotInPossibles = true;
				return true;
			}
		}
		return isNotInPossibles;

	}

	// Returns the size of the List as we need it to obtain a random number within
	// the limits
	public int getSize() {
		return getPossibles().size();
	}

	public List<Pair<Integer, Integer>> getPossibles() {
		return possibles;
	}

	public int getBoardSize() {
		return this.boardSize;
	}

}
