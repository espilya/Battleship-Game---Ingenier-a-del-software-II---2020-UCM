package simulator.observer;

import utils.Pair;

/**
 *  The interface for the observer object 
 */
public interface IObserver {

	/**
	 * 
	 * @param pos
	 * @param points
	 */
	public void update(Pair<Integer, Integer> pos, int points);
}
