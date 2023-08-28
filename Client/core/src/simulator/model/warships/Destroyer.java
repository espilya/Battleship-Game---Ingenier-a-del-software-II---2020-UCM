package simulator.model.warships;

import java.util.LinkedHashMap;

public class Destroyer extends Ship {
	private static final long serialVersionUID = 7492223682000470441L;

	/**
	 * Constructor for the Destroyer
	 * 
	 * @param id
	 */
	public Destroyer(String id) {
		super(id);
		size = 2;
		aliveCells = size;
		type = "Destroyer";
		cellsLocation = new LinkedHashMap<>();
	}

}
