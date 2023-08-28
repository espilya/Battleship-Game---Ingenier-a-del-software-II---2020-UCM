import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.controller.LocalController;
import simulator.model.BotDifficulty;
import simulator.model.Computer;
import simulator.model.GameSize;
import simulator.model.strategies.EasyComputerAttackStrategy;
import simulator.model.strategies.HardComputerAttackStrategy;
import utils.Pair;
/* Prueba de la interfaz IComputerAttackStrategy,
 * probando el modo facil y el hard del computador
 * */

class ComputerAttackStrategyTest {
	
	
	private Computer c,c2;
	private LocalController lc, lc2;
	private Pair<Integer, Integer> pos;
	private EasyComputerAttackStrategy str;
	private HardComputerAttackStrategy str2;
	
	@BeforeEach
	public void before() {
		lc = new LocalController(GameSize.CLASSIC,BotDifficulty.EASY);
		c = new Computer("test",GameSize.CLASSIC,BotDifficulty.EASY,lc);
		str = new EasyComputerAttackStrategy(lc);
		
		lc2 = new LocalController(GameSize.INSANE,BotDifficulty.EASY);
		c2 = new Computer("test",GameSize.INSANE,BotDifficulty.HARD,lc2);
		str2 = new HardComputerAttackStrategy(lc2);
		
	}
	@Test
	void huntTest() {
		boolean  expected = true;
		 pos = str.hunt(c);
		 assertEquals(expected, pos.getFirst() >=0 && pos.getFirst()< GameSize.CLASSIC.getSize() && pos.getSecond()>=0 && pos.getSecond()< GameSize.CLASSIC.getSize());
		 
		 pos = str2.hunt(c2);
		 assertEquals(expected, pos.getFirst() >=0 && pos.getFirst()< GameSize.INSANE.getSize() && pos.getSecond()>=0 && pos.getSecond()< GameSize.INSANE.getSize());
	}

}
