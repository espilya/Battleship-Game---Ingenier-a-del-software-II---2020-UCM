import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.controller.GameResponse;
import simulator.model.GameMode;
import simulator.model.GameSize;
import simulator.model.Player;
import simulator.model.warships.Battleship;
import simulator.model.warships.PatrolBoat;
import simulator.model.warships.Ship;
import utils.Pair;

public class PlayerTest {

	private Player p, g, j;
	private Pair<Integer, Integer> pos, pos1;

	@BeforeEach
	public void before() {

		p = new Player("p", GameSize.CLASSIC);
		g = new Player("g", GameSize.INSANE);
		j = new Player("j", GameSize.REDUCED);
		
		pos = new Pair<Integer, Integer>(5, 5);
		pos1 = new Pair<Integer, Integer>(50, 79);

	}

	@Test
	public void isGetGenControlTest() {
		boolean esperado = true;
		boolean resultado = p.getGenControl().isNotInPossibles(pos);
		boolean esperado1 = false;
		boolean res = p.getGenControl().isNotInPossibles(pos1);

		assertEquals(esperado, resultado);
		assertEquals(esperado1, res);

	}

	
	@Test
	public void receiveNormalAttackTest() {
		assertEquals(GameResponse.MISS, p.receiveNormalAttack(pos));
	}
	
	@Test
	public void probarEscudo() {
		int[]  cell = new int[2];
		cell[1]= 5;
		cell[0]= 5;
		Battleship s = new Battleship("B");
		p.placeShip(cell, s, false);
		
		p.reciveDefenseShip(pos);
	}

}/**
	 * *Todo esto tambien se puede aplicar al Computer.java* <br>
	 * - Se puede hacer test de los diferentes ataques, en linea, AoE, normal. <br>
	 * - 'update()', 'placeShip()'. <br>
	 * --Ilya--
	 */