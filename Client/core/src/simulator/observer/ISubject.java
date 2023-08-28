package simulator.observer;

import utils.Pair;

/**
 * The interface for the Subject object
 */
public interface ISubject {

	/**
	 * 
	 * @param observer
	 */
	public void registerObserver(IObserver observer);

	/**
	 * 
	 * @param observer
	 */
	public void unregisterObserver(IObserver observer);

	/**
	 * 
	 * @param pos
	 * @param points
	 */
	public void notifyObserver(Pair<Integer, Integer> pos, int points);
}
