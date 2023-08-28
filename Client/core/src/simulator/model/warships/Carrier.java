package simulator.model.warships;

import java.util.LinkedHashMap;

public class Carrier extends Ship{
	private static final long serialVersionUID = 2042619784760593411L;

	/**
	 * Constructor for the Carrier
	 * @param id
	 */
	Carrier(String id) {
		super(id);
		size = 6;
		aliveCells = size;
		type = "Carrier";
		cellsLocation = new LinkedHashMap<>();
	}


}
