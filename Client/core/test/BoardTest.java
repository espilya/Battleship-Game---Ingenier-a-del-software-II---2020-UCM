import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Timeout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import simulator.model.Board;
import simulator.model.GameSize;
/*probando los parameterized para probar mas datos a la vez*/
import simulator.model.warships.Ship;
import utils.Pair;

public class BoardTest {

	private Board b;
	private Pair<Integer, Integer> pos, pos1, pos2, pos3;

	@BeforeEach
	public void before() {
		b = new Board(GameSize.CLASSIC);
		b.create();
		pos = new Pair<Integer, Integer>(2, 3);
		pos1 = new Pair<Integer, Integer>(10, 3);
		pos2 = new Pair<Integer, Integer>(0, 0);
		pos3 = new Pair<Integer, Integer>(6, 9);
	}

	@TestFactory
	Collection<DynamicTest> dynamicTestCheckPos() {

		return Arrays.asList(dynamicTest("Prueba 1, check position", () -> assertTrue(b.checkPosition(2, 1, 2, 5))),
				dynamicTest("Prueba 2, check position", () -> assertEquals(b.checkPosition(4, 1, 10, 10), false)),
				dynamicTest("Prueba 3, check position", () -> assertFalse(b.checkPosition(6, 0, 9, 9))),
				dynamicTest("Prueba 4, check position", () -> assertTrue(b.checkPosition(1, 0, 0, 0))));
	}

	@TestFactory
	Collection<DynamicTest> dynamicTestHasHit() {

		return Arrays.asList(dynamicTest("Prueba 1, check position", () -> assertEquals(b.posHasHit(pos), -1)),
				dynamicTest("Prueba 3, check position", () -> assertEquals(b.posHasHit(pos2), -1)),
				dynamicTest("Prueba 4, check position", () -> assertEquals(b.posHasHit(pos3), -1)));
	}

	@TestFactory
	Collection<DynamicTest> dynamicTestIsHit() {

		return Arrays.asList(dynamicTest("Prueba 1, Is Hit", () -> assertFalse(b.isHit(pos))),
				dynamicTest("Prueba 3, Is Hit", () -> assertFalse(b.isHit(pos2))),
				dynamicTest("Prueba 4, Is Hit", () -> assertFalse(b.isHit(pos3))));
	}

	// probamos que lanza una excepcion en caso de que pos sea una posicion fuera
	// del tablero
	@DisplayName("Debe lanzar la Exception")
	@Test
	public void hasHitTestException() {

		try {
			b.posHasHit(pos1);
			fail("se esperaba Exception");
		} catch (Exception e) {

		}

	}

	@DisplayName("Debe lanzar la Exception al comprobar si se puede hacer HIT")
	@Test
	public void isHitTestException() {

		try {
			b.isHit(pos1);
			fail("se esperaba Exception");
		} catch (Exception e) {

		}

	}

	@Test
	public void randomShipPlacementTest() {
		// probar que no supera de 2 milisegundos la ejecucion de esta fuuncion

		assertTimeout(Duration.ofMillis(40), () -> b.randomShipPlacement());
	}

	@Test
	public void updateBoardTest() {
		assertTimeout(Duration.ofMillis(30), () -> b.updateBoard(0, 0, 2));

	}

}
