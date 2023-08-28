import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.model.GameMode;
import simulator.model.GameSize;
import simulator.model.Player;
import utils.GenerationControl;
import utils.Pair;

public class GenerationControlTest {

	/*
	 * Tambien hay que crear test sobre la IA, sobre la easy, normal y hard
	 * 
	 * -- Luque --
	 */

	private GenerationControl g;
	private Pair<Integer, Integer> pos, pos1;

	@BeforeEach
	public void before() {
		g = new GenerationControl(GameSize.INSANE);
		pos = new Pair<Integer, Integer>(13, 12);

		pos1 = new Pair<Integer, Integer>(35, 79);
	}

	@Test
	public void isGetGenControlTest() {
		boolean esperado = true;
		boolean resultado = g.isNotInPossibles(pos);
		boolean esperado1 = false;
		boolean res = g.isNotInPossibles(pos1);

		assertEquals(esperado, resultado);
		assertEquals(esperado1, res);

	}

}
