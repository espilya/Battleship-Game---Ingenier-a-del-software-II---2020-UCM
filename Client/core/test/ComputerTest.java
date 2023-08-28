import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.utils.Array;

import simulator.controller.GameResponse;
import simulator.controller.LocalController;
import simulator.model.BotDifficulty;
import simulator.model.Computer;
import simulator.model.GameMode;
import simulator.model.GameSize;
import simulator.model.warships.Battleship;
import utils.Pair;

class ComputerTest {

	private Computer c;
	private Computer c2;
	private LocalController lc;
	private LocalController lc2;
	private Pair<Integer, Integer> pos, pos1;
	
	
	

	@BeforeEach
	public void before() {
		lc = new LocalController(GameSize.CLASSIC,BotDifficulty.EASY);
		lc2 = new LocalController(GameSize.INSANE,BotDifficulty.EASY);
		c = new Computer("test", GameSize.CLASSIC, BotDifficulty.EASY, lc);
		c2 = new Computer("test", GameSize.INSANE, BotDifficulty.EASY, lc2);
		pos = new Pair<Integer, Integer>(5, 5);
		pos1 = new Pair<Integer, Integer>(3, 3);
		
	}

	// probamos que los metodosno gasten un tiempo excedido

	@Test
	public void askRandomTest() {

		assertTimeout(Duration.ofMillis(40), () -> c.askRandomPlacement());
	}

	@Test
	public void askAttackAction() {
		assertTimeout(Duration.ofMillis(40), () -> c.askAttackAction(lc));
	}
	@Test
	public void receiveNormalAttackTest()
	{
		assertEquals(GameResponse.MISS, c.receiveNormalAttack(pos));
	}
	/*Para probar hacemos un ataque a una línea y luego repetimos ese mismo ataque, y comprobamos que nos devuelve miss*/
	@Test
	public void receiveLineAttackTest()
	{
		c.askRandomPlacement();
		c.receiveLineAttack(pos1, true );
		assertEquals(GameResponse.MISS, c.receiveLineAttack(pos1, true ));
	}

}
