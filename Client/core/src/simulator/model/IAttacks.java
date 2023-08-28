package simulator.model;

import simulator.controller.GameResponse;
import utils.Pair;

/**
 * Interface for the receiving of different attacks. Implemented in the Player class
 */
public interface IAttacks {
	/**
	 * 
	 * @param pos
	 * @return GameResponse
	 */
	public GameResponse receiveNormalAttack(Pair<Integer, Integer> pos);

	/**
	 * 
	 * @param pos
	 * @param receiveLineAttack
	 * @return GameResponse
	 */
	public GameResponse receiveLineAttack(Pair<Integer, Integer> pos, boolean receiveLineAttack);

	/**
	 * 
	 * @param pos
	 * @return GameResponse
	 */
	public GameResponse receiveAOEAttack(Pair<Integer, Integer> pos);
}
