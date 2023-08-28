import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.model.Board;
import simulator.model.GameMode;
import simulator.model.GameSize;
import simulator.model.Player;
import simulator.model.warships.Battleship;
import simulator.model.warships.Cell;
import simulator.model.warships.Destroyer;
import simulator.model.warships.Ship;
import utils.Pair;

class ShipTest {
	/*
	 * - Test que ya funciona.
	 */
	Battleship b = new Battleship("Test");

	@BeforeEach
	public void before() {

		for (int i = 0; i < b.getSize(); i++) {
			Cell c = new Cell(i, i + 1, "id");
			b.placeShipCells(c);

		}
	}

	@Test
	void hitTest() {

		b.setAliveCells(1);
		boolean esperado = true;
		boolean res = b.hit(2, 3);
		assertEquals(esperado, res);
	}

}
