package simulator.model.strategies;

import java.io.Serializable;

import simulator.controller.LocalController;
import simulator.model.Computer;
import utils.Pair;

/**
 * Hard computer strategy
 *
 */
public class HardComputerAttackStrategy implements IComputerAttackStrategy, Serializable {
	private static final long serialVersionUID = 8069723816710070577L;

	
	int boardSize;
	/**
	 * 
	 * @param game
	 */
	public HardComputerAttackStrategy(LocalController game) {
		boardSize = game.getGameType().getSize();
	}

	// The only change that between the Normal mode and the Hard mode is that in the
	// Hard mode the computer attacks two shots at once. This is implemented in the
	// gameLoop
	@Override
	public Pair<Integer, Integer> computerAttackActionAlgorithm(Computer computer) {

		if (!computer.isHunt()) {
			return target(computer.getLastShot(), computer);
		} else
			return hunt(computer);

	}

}
