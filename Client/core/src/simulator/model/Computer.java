package simulator.model;

import java.io.Serializable;

import org.json.JSONObject;

import simulator.controller.LocalController;
import simulator.model.strategies.EasyComputerAttackStrategy;
import simulator.model.strategies.HardComputerAttackStrategy;
import simulator.model.strategies.IComputerAttackStrategy;
import simulator.model.strategies.NormalComputerAttackStrategy;
import simulator.model.warships.Cell;
import utils.Pair;

/**
 * This class encapsulates the behaviour of the computer player. This is only given when the game mode is set to PvE
 */
public class Computer extends Player implements Serializable {
	private static final long serialVersionUID = 4905148519153092548L;

	private Pair<Integer, Integer> lastShot;
	private IComputerAttackStrategy strategy;
	private boolean hunt = true;
	private int cont = -1;
	private BotDifficulty difficulty;

// -------------------------------------------------------------------------- Construtor --------------------------------------------------------------------------	
	public Computer(String id, GameSize tam, BotDifficulty difficulty, LocalController game) {
		super("Computer", tam);
		this.difficulty = difficulty;
		strategy = computerActionStrategySelector(game);
	}

// -------------------------------------------------------------------------- IObserver implementation --------------------------------------------------------------------------

	/**
	 * Updates the necessary elements contained by the player.
	 * 
	 * If int p is greater that 0 it means that a ship was destroyed. That is why we set the given cell to KILLED_CELL
	 * @param pos
	 * @param p
	 */
	@Override
	public void update(Pair<Integer, Integer> pos, int p) {
		genControl.removeFromPossibles(pos);
		if (p > 0) {
			// Que pasara si es kill
			if(hunt)
				prepareTargetMode(pos);
			points += p;
			historial[pos.getFirst()][pos.getSecond()].set(Cell.KILLED_CELL);
		} else if (p == 0) {
			// Que pasara si es solo hit
			if(hunt)
				prepareTargetMode(pos);
			historial[pos.getFirst()][pos.getSecond()].set(Cell.KILLED_CELL);
		} else {
			// Que pasara si es un miss
		}
	}

// -------------------------------------------------------------------------- Placement interactions --------------------------------------------------------------------------

	/**
	 * Manages the placement of the ships in a random order.
	 */
	@Override
	public void askRandomPlacement() {
//		System.out.println("Computer: askShipPlacement()");
		board.randomShipPlacement();
		board.toString();
	}

// -------------------------------------------------------------------------- Attack interactions --------------------------------------------------------------------------

	/**
	 * This method is in charge of the attack behaviour of the computer.
	 * Depending on the level chosen it will have one behaviour or another
	 * @param game
	 * @return computerAttackAction(game)
	 */
	public Pair<Integer, Integer> askAttackAction(LocalController game) {
		return computerAttackAction(game);
	}

	/**
	 * This method is the one called by the controller to manage the attack of the computer
	 * The last shot is assigned to lastShot which in turn is used for the higher levels of computerAttackAction(game)
	 * @param game
	 * @return pos
	 */
	private Pair<Integer, Integer> computerAttackAction(LocalController game) {
//		System.out.println("Computer: computerAttackAction()");
		Pair<Integer, Integer> pos = strategy.computerAttackActionAlgorithm(this);
		lastShot = pos;
		return pos;
	}

	/**
	 * Depending on the level of the game the instance of ComputerAttackStrategy is chosen
	 * @param game
	 * @return IComputerAttackStrategy
	 */
	private IComputerAttackStrategy computerActionStrategySelector(LocalController game) {
		if (difficulty.equals(BotDifficulty.EASY))
			return new EasyComputerAttackStrategy(game);
		else if (difficulty.equals(BotDifficulty.NORMAL))
			return new NormalComputerAttackStrategy(game);
		else
			return new HardComputerAttackStrategy(game);
	}

	public Pair<Integer, Integer> getLastShot() {
		return lastShot;
	}
	
	public void prepareTargetMode(Pair<Integer, Integer> pos) {
		lastShot = pos; //This is the centre of attack from now on
		hunt = false; //The computer is now in hunt mode and will attack the cells adjacent to lasShot
		cont = 0; //The counter for the number of attack is started
	}

	// ----save / load-----

	public JSONObject report() {
		JSONObject ob = new JSONObject();
		ob.put("id", id);
		ob.put("points", points);
		ob.put("board", board.report());
		ob.put("BotDifficulty", difficulty);
		return ob;
	}

	public void load(JSONObject jo) {
		id = jo.getString("id");
		points = jo.getInt("points");
		board.load(jo.getJSONObject("board"));
		difficulty = BotDifficulty.valueOf(jo.getString("BotDifficulty"));

	}

// -------------------------------------------------------------------------- Getters and Setters --------------------------------------------------------------------------
	
	public boolean isHunt() {
		return hunt;
	}

	public void setHunt(boolean hunt) {
		this.hunt = hunt;
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
	}

	public void incrementCont() {
		this.cont++;
	}
	
	public void resetCont() {
		this.cont = -1;
		this.hunt = true;
	}
}
