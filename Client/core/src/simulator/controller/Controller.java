package simulator.controller;

import java.io.Serializable;
import java.util.List;

import simulator.model.GameSize;
import simulator.model.Player;
import simulator.model.warships.Cell;
import simulator.model.warships.Ship;
import utils.Pair;

/**
 * This Controller class is abstract because we have a different controllers
 * depending on the game mode. <br>
 * - For PvP we will use OnlineController as we will need to make use of server
 * and P2P capabilities. <br>
 * - For PvE we will use LocalController as we don't need to make use of server
 * and P2P capabilities and one of the players is Computer by default. <br>
 */
public abstract class Controller implements Serializable {
	private static final long serialVersionUID = -4214531784345787718L;

	protected Player player;
	protected String enemyPlayer;
	protected GameSize gameSize;
	protected GameState gameState;

	public enum GameState {
		PLAY, WAIT_ATTACK, WON, LOSE, PLACE, WAIT_PLACE, ENEMY_EXIT, LOST_CONNECTION;
	};

	/**
	 * Constructor the only thing that is set is the size of the game.
	 * 
	 * @param gameSize
	 */
	public Controller(GameSize gameSize) {
		this.gameSize = gameSize;
	}

// ----- Game State methods. To determine the state of the game and various other methods to do with it ----- 

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Placement interactions ----------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */
	/**
	 * Manage the placement of ship
	 * 
	 * @param cellLocation x and y position of the first cell
	 * @param ship         The ship
	 * @param horizontal   True if horizontal
	 */
	public abstract void placeShip(int[] cellLocation, Ship ship, boolean horizontal);

	public abstract void randomPlacement();

	/*
	 * -----------------------------------------------------------------------------
	 * ---------------------------- --------------------------- Fire interactions
	 * ---------------------------
	 * -----------------------------------------------------------------------------
	 * ----------------------------
	 */
	/**
	 * More description in implementation. At OnlineController and LocalController
	 * 
	 * @param x          position
	 * @param y          position
	 * @param fireType   '0 = normal shot , 1 = attack line, 2 = AoE shot'
	 * @param horizontal true if horizontal
	 * @return GameResponse Response to Attack
	 * @throws Exception
	 */
	public abstract GameResponse fire(int x, int y, int fireType, boolean horizontal) throws Exception;

	/**
	 * More description in implementation. At OnlineController and LocalController
	 * 
	 * @param x          position
	 * @param y          position
	 * @param fireType   '0 = normal shot , 1 = attack line, 2 = AoE shot'
	 * @param horizontal true if horizontal
	 * @return GameResponse Response to Attack
	 * @throws Exception
	 */
	public abstract GameResponse autoFire(int x, int y, int fireType, boolean horizontal) throws Exception;

	/**
	 * If player have enough points, we set shield at x,y ship or cell setShield
	 * ship = 8 setShield cell = 5
	 * 
	 * @param x          position
	 * @param y          position
	 * @param typeShield shield type: (0 == ship shield, 1 == cell shield )
	 * @throws Exception
	 */
	public void setShield(int x, int y, int typeShield) throws Exception {
		final int shipShieldCost = 2;
		final int cellShipCost = 1;
		Pair<Integer, Integer> pos = new Pair<Integer, Integer>(x, y);
		if (typeShield == 0) {
			if (player.getPoints() >= shipShieldCost) {
				player.reciveDefenseShip(pos);
				player.buySkills(shipShieldCost);
			}
		} else if (typeShield == 1) {
			if (player.getPoints() >= cellShipCost) {
				player.receiveDefenseCell(pos);
				player.buySkills(cellShipCost);
			}

		} else {
			throw new Exception("invalid shield type");
		}
	}

	/**
	 * Used to buy line attack and AoE attack Cost: <br>
	 * line attack = 6 points <br>
	 * AoE attack = 4 points
	 * 
	 * @param type '1 = attack line, 2 = AoE shot'
	 * @return True - if player can afford to buy the attack
	 */
	public boolean buyAttack(int type) {
		final int lineAttackCost = 6;
		final int aoeAttackCost = 4;
		if (type == 1) {
			if (player.getPoints() >= lineAttackCost) {
				player.buySkills(lineAttackCost);
				return true;
			}
		} else if (type == 2) {
			if (player.getPoints() >= aoeAttackCost) {
				player.buySkills(aoeAttackCost);
				return true;
			}
		}
		return false;
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Getters && Setters ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	public GameState getState() {
		return gameState;
	}

	public void setState(GameState state) {
		gameState = state;
	}

	public abstract List<Ship> getShipsList();

	public abstract Cell[][] getPlayerBoard();

	public abstract Cell[][] getEnemyBoard();

	/**
	 * @return the gameSize
	 */
	public GameSize getGameSize() {
		return gameSize;
	}

	/**
	 * ---------------------------------------------------------------------------------------------------------
	 * --------------------------- Other interactions ---------------------------
	 * ---------------------------------------------------------------------------------------------------------
	 */

	public abstract void setPlacementAsFinished();

	public int getPoints() {
		return this.player.getPoints();
	}

	public abstract void reset();

	public abstract void exit();

}
