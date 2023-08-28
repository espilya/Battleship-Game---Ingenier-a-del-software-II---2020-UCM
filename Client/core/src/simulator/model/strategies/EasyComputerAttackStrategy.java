package simulator.model.strategies;

import java.io.Serializable;

import simulator.controller.LocalController;
import simulator.model.Computer;
import utils.Pair;


/**
 * Easy computer strategy
 *
 */
public class EasyComputerAttackStrategy implements IComputerAttackStrategy, Serializable {
	private static final long serialVersionUID = 4455282170505174832L;
	
	int boardSize;

	/** 
	 * Constructor for the EasyAttackStrategy.
	 * @param game
	 */
	public EasyComputerAttackStrategy(LocalController game) {
		boardSize = game.getGameType().getSize();
	}

	// The Easy mode has the computer attacking at random cells and the genControl
	// class makes it so you can't attack previously attacked positions

	@Override
	public Pair<Integer, Integer> computerAttackActionAlgorithm(Computer computer) {
		return hunt(computer);
	}

}
