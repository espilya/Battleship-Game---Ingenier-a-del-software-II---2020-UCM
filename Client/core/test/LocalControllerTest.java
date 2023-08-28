import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.controller.GameResponse;
import simulator.controller.LocalController;
import simulator.model.BotDifficulty;
import simulator.model.GameMode;
import simulator.model.GameSize;
import utils.Pair;

public class LocalControllerTest {
	/**
	 * Seguramente va cambiar porque vamos usar una interfaz, y tendremos que
	 * unificar o separa el test para los dos controller's (Se puede ver en internet
	 * como se hace test para clases parecidas que implementan una interfaz) <br>
	 * <br>
	 * - Se puede crear un juego PvE, poner los barcos de forma random llamando las
	 * funciones necesarias, despues hacer un fire/autofire sobre una casilla N, y
	 * despues repetir el mismo fire, y compobar que la respuesta sea invalida. <br>
	 * <br>
	 * - Repetir lo anterior, pero en vez de testear los fire's podemos testear si
	 * el juego antes de guardar y despues de leer el mismo juego, (atributos tienen
	 * mismo valor, el tablero es igual, etc) <br>
	 * <br>
	 * - Se puede hacer test de las funciones 'playerHasBeenHit()', 'init()'
	 * comprobar que despues de esta funcion los valores no son null <br>
	 * --Ilya--
	 */
	private LocalController c;
	Pair<Integer, Integer> pos;

	@BeforeEach
	public void before() {
		c = new LocalController(GameSize.CLASSIC,BotDifficulty.EASY);
		pos = new Pair<Integer, Integer>(5, 2);
	}
	
	
	@Test
	public void fireTest() throws Exception {
		GameResponse g = null, n = null;
		GameResponse esperado = GameResponse.INVALID;
		c.randomPlacement();
		n = c.fire(5, 5, 0, false);
		g = c.fire(5, 5, 0, false);

		assertEquals(esperado, g);

	}

	// comprobamos que el fichero guardado es igual al fichero despues cargado
	/*
	 * @Test public void ReportTest(){ c.randomPlacement(); JSONObject save =
	 * c.report(); c.save();
	 * 
	 * c.load(); JSONObject load = c.report();
	 * 
	 * assertEquals(save,load);
	 * 
	 * }
	 */
	
	/**
	 * 	-- Luque --
	 *  Para el local controller es importante chequear los metodos que se usarian para comunicar informacion
	 */
	

}
