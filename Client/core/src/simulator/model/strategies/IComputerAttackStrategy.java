package simulator.model.strategies;

import java.util.concurrent.ThreadLocalRandom;

import simulator.model.Computer;
import utils.Pair;

/**
 * Interface for IA attacks
 */
public interface IComputerAttackStrategy {
	
	/**
	 * 
	 * @param computer
	 * @return pos
	 */
	Pair<Integer, Integer> computerAttackActionAlgorithm(Computer computer);

	
	
 /**
  * Basic actions that are common for all the different computer attack strategies no matter the level.
  * This are put in the interface as default methods to remove the interdependencies between the strategies
  * and a proper implementation of the Strategy Pattern.
  */
	
	/*
	 * This is the class upon the most basic computer attack interactions are based
	 * on. It generates a random number which will be used as the index for which
	 * the next attack position will be selected. Once this position is selected it
	 * is removed from the possible attack position therefore limiting the computer
	 * and not letting it perform an attack on a previously attacked cell.
	 * 
	 * The way it was first introduced showed problems when getting to later stages
	 * of the game as it worked with a while and it eventually entered a infinite
	 * loop. Therefore we saw more fitting to have a Class GenerationControl that
	 * would help us keep track of the attack spot generation
	 */
	/**
	 * 
	 * @param computer
	 * @return pos
	 */
	public default Pair<Integer, Integer> hunt(Computer computer) {

		Pair<Integer, Integer> pos = computer.getGenControl().getPossibles()
				.get(ThreadLocalRandom.current().nextInt(0, computer.getGenControl().getSize()));

		computer.getGenControl().removeFromPossibles(pos);

		return pos;

	}
	
	/**
	 * A passing function that will return the Pair (x, y) as the position for the
	 * next shot
	 * 
	 * @param x position
	 * @param y position
	 * @return pos Pair<Integer, Integer> position
	 */
	public default Pair<Integer, Integer> attack(int x, int y) {
		Pair<Integer, Integer> pos = new Pair<Integer, Integer>(x, y);
		return pos;
	}
	
	/**
	 * The target mode is in charge of keeping track of which adjacent cells have
	 * been attacked and which ones are still pending of attack It work with a
	 * simple counter that will increment after each shot. Also before actually
	 * attacking the cell it will check if the shot can be made or if the shot that
	 * it is planing is not possible. This is to make the attack algorithm more
	 * efficient and not waste a shot attacking out of bounds or an already attacked
	 * shot
	 * 
	 * @param pair Pair<Integer, Integer> 
	 * @param computer bot player
	 * @return position Pair<Integer, Integer> 
	 */
	public default Pair<Integer, Integer> target(Pair<Integer, Integer> pair, Computer computer) {

		Pair<Integer, Integer> above = new Pair<Integer, Integer>(pair.getFirst(), pair.getSecond() + 1);
		Pair<Integer, Integer> below = new Pair<Integer, Integer>(pair.getFirst(), pair.getSecond() - 1);
		Pair<Integer, Integer> right = new Pair<Integer, Integer>(pair.getFirst() + 1, pair.getSecond());
		Pair<Integer, Integer> left = new Pair<Integer, Integer>(pair.getFirst() - 1, pair.getSecond());

		// A switch statement to determine where the attack will be made when in the
		// target mode
		switch (computer.getCont()) {

		case 0:
			if (computer.getGenControl().isNotInPossibles(above)) {
				computer.incrementCont();
				break;
			} else {
				computer.incrementCont();
				computer.getGenControl().removeFromPossibles(above);
				return attack(above.getFirst(), above.getSecond());
			}

		case 1:
			if (computer.getGenControl().isNotInPossibles(below)) {
				computer.incrementCont();
				break;
			} else {
				computer.incrementCont();
				computer.getGenControl().removeFromPossibles(below);
				return attack(below.getFirst(), below.getSecond());
			}

		case 2:
			if (computer.getGenControl().isNotInPossibles(right)) {
				computer.incrementCont();
				break;
			} else {
				computer.incrementCont();
				computer.getGenControl().removeFromPossibles(right);
				return attack(right.getFirst(), right.getSecond());
			}

		case 3:
			if (computer.getGenControl().isNotInPossibles(left)) {
				computer.resetCont();
				break;
			} else {
				computer.resetCont();
				computer.getGenControl().removeFromPossibles(left);
				return attack(left.getFirst(), left.getSecond());
			}

		default:
			System.out.println("We are in target mode this scenario should not happen");
		}
		return above;
	}
	
}
