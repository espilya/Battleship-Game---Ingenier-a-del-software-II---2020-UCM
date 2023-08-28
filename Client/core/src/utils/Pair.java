package utils;

import java.io.Serializable;

/**
 * Generic data container used in the game.
 * 
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2> implements Serializable {
	private static final long serialVersionUID = -2444848150452147685L;

	private T1 _first;
	private T2 _second;

	/**
	 * 
	 * @param first
	 * @param second
	 */
	public Pair(T1 first, T2 second) {
		_first = first;
		_second = second;
	}

	/**
	 * 
	 * @return _first
	 */
	public T1 getFirst() {
		return _first;
	}

	/**
	 * 
	 * @return _second
	 */
	public T2 getSecond() {
		return _second;
	}

	public String toString() {

		return _first + "," + _second;
	}

	/**
	 * @param first
	 * @return _first
	 */

	public void setFirst(T1 first) {
		this._first = first;
	}

	/**
	 * @param second
	 * @return _second
	 */
	public void setSecond(T2 second) {
		this._second = second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_first == null) ? 0 : _first.hashCode());
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (_first == null) {
			if (other._first != null)
				return false;
		} else if (!_first.equals(other._first))
			return false;
		if (_second == null) {
			if (other._second != null)
				return false;
		} else if (!_second.equals(other._second))
			return false;
		return true;
	}

}
