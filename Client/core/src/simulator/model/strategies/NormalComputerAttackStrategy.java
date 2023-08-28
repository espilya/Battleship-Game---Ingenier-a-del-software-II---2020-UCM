package simulator.model.strategies;

import java.io.Serializable;

import simulator.controller.LocalController;
import simulator.model.Computer;
import utils.Pair;

/**
 * Normal computer strategy: <br>
 * The way the NormalComputerAttackStrategy works is the following: <br>
 * + It will attack randomly searching for a target and will do this until it
 * has hit a target. <br>
 * + When a target has been hit it will go into Target mode. In this mode the
 * computer will attack to the cells adjacent to the target hit where unless a
 * patrolBoat is hit the number of hits will always be at least > 1 <br>
 * + After all the adjacent cells have been shot we will return to hunt mode and
 * the process is repeated
 * 
 */
public class NormalComputerAttackStrategy implements IComputerAttackStrategy, Serializable {
	private static final long serialVersionUID = -5686137513514612412L;
	
	int boardSize;

	/**
	 * 
	 * @param game
	 */
	public NormalComputerAttackStrategy(LocalController game) {
		boardSize = game.getGameType().getSize();
	}

	/**
	 * @param computer Bot player
	 */
	@Override
	public Pair<Integer, Integer> computerAttackActionAlgorithm(Computer computer) {

		if (!computer.isHunt() || computer.getCont() != -1) {
			return target(computer.getLastShot(), computer);
		} else
			return hunt(computer);

	}

}
