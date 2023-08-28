package simulator.model.warships;

import java.util.LinkedHashMap;

public class PatrolBoat extends Ship {
	private static final long serialVersionUID = -8746730211561817524L;

	/**
	 * Constructor for the Patrol Boat
	 * 
	 * @param id
	 */
	public PatrolBoat(String id) {
		super(id);
		size = 1;
		aliveCells = size;
		type = "PatrolBoat";
		cellsLocation = new LinkedHashMap<>();
	}

}
