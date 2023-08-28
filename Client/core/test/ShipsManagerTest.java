import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.*;

import simulator.model.GameSize;
import simulator.model.warships.Cell;

import simulator.model.warships.Ship;
import simulator.model.warships.ShipsManager;
import utils.Pair;

@DisplayName("ShipManager Test")
class ShipsManagerTest {

	ShipsManager sp = new ShipsManager();

	@BeforeEach
	public void before() {
		sp.createShips(GameSize.CLASSIC);
	}

	// Este test comprueba que se lanza una excepcion al ejecutar el metodo
	@DisplayName("Debe lanzar la Exception")
	@Test
	public void shipPlaceTest() {
		Ship s = sp.getShip("P0");
		try {
			sp.shipPlace(s, null);
			fail("se esperaba Exception");
		} catch (Exception e) {

		}

	}

	@Test
	public void manageHitTest() {
		Pair<Integer, Integer> pos = new Pair<>(0, 1);

		Ship s = sp.getShip("P0");
		// colocamos el barco en la celda

		for (int i = 0; i < s.getSize(); i++) {
			Cell c = new Cell(i, i + 1, "id");
			s.placeShipCells(c);

		}
		// boolean que debería devolver el test
		boolean esperado = true;
		// boolean que devuelve al probarr la función
		boolean res = sp.manageHit("P0", pos);

		assertEquals(esperado, res);

	}

}
