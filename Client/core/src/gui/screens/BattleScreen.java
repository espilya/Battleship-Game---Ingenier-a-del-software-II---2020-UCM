package gui.screens;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import gui.game.HundirLaFlota;
import gui.helpers.Button;
import gui.helpers.ShipPlacer;
import gui.helpers.ViewPort;
import simulator.controller.Controller;
import simulator.controller.Controller.GameState;
import simulator.controller.GameResponse;
import simulator.controller.LocalController;
import simulator.controller.OnlineController;
import simulator.model.BotDifficulty;
import simulator.model.GameMode;
import simulator.model.GameSize;
import simulator.model.warships.Cell;

/**
 * Main battle screen. User for represent and manage the battle.
 *
 */
public class BattleScreen extends ApplicationAdapter implements AnimationListener, InputProcessor, Screen {

	private Controller controller;

	// UI
	private Skin skin;
	private SpriteBatch batch;
	private BitmapFont font;
	private CharSequence statusBar, saveBar, pointsBar, enemyIdBar;
	private ViewPort yourBoard, enemyBoard;

	private int boardSize;

	/**
	 * Sprite
	 */
	private Texture waterSprite, hitSprite, sunkSprite, missSprite, backgroundSprite, logoSprite, musicSprite,
			musicNoteCancelledSprite, rotateSprite, popupSprite, gameWonSprite, gameLostSprite, backSprite, undoSprite,
			saveSprite, randomPlacementSprite, lineAttackSprite, AoEAttackSprite;

	/**
	 * Sprite for normal ships
	 */
	private Texture shipStartNormalH, shipStartNormalV, shipCenterNormalH, shipCenterNormalV, shipEndNormalH,
			shipEndNormalV, shipSingleNormalV, shipSingleNormalH;

	/**
	 * Sprite for shields ships
	 */
	private Texture shipStartShieldH, shipStartShieldV, shipCenterShieldH, shipCenterShieldV, shipEndShieldH,
			shipEndShieldV, shipSingleShieldH, shipSingleShieldV, shieldHit;

	// Sounds & music
	private Music music;
	private Sound explosion;
	private boolean playSound;

	/**
	 * Buttons for interactions with the GUI
	 */
	private Button musicButton, rotateButton, backButton, undoButton, randomPlacementButton;
	private Button saveButton;
	/**
	 * Attack Buttons
	 */
	private Button lineAttackButton, aoeButton;

	/**
	 * Ship placer
	 */
	private ShipPlacer shipPlacer;

	private GameSize gameSize;
	private GameMode gameMode;
	private BotDifficulty botDifficulty;
	private int fireType;
	private HundirLaFlota game;

	/**
	 * Used for normal PvE execution.
	 * 
	 * @param game
	 */
	public BattleScreen(HundirLaFlota game) {
		this.game = game;
		gameSize = GameSize.valueOf(Gdx.app.getPreferences(HundirLaFlota.TITLE).getString("GameSize"));
		gameMode = GameMode.PVE;
		botDifficulty = BotDifficulty.valueOf(Gdx.app.getPreferences(HundirLaFlota.TITLE).getString("BotDifficulty"));
		controller = new LocalController(gameSize, botDifficulty);
		init();
	}

	/**
	 * Used for PvP execution.
	 * 
	 * @param game
	 * @param Onlinecontroller
	 */
	public BattleScreen(HundirLaFlota game, OnlineController Onlinecontroller) {
		this.game = game;
		this.controller = Onlinecontroller;
		gameSize = controller.getGameSize();
		gameMode = GameMode.PVP;
		init();
	}

	/**
	 * Used for PvE loaded game
	 * 
	 * @param game
	 * @param Localcontroller
	 */
	public BattleScreen(HundirLaFlota game, LocalController Localcontroller) {
		this.game = game;
		this.controller = Localcontroller;
		gameSize = controller.getGameSize();
		gameMode = GameMode.PVE;
		playSound = true;
		boardSize = gameSize.getSize();
		fireType = 0;
	}

	private void init() {
		playSound = true;
		boardSize = gameSize.getSize();
		fireType = 0;
		controller.setState(GameState.PLACE);
		statusBar = "PLACE SHIPS";
		if (gameMode == GameMode.PVE)
			enemyIdBar = botDifficulty.name();
		else
			enemyIdBar = ((OnlineController) controller).getEnemyId();
	}

	/**
	 * Solo se ejecuta al cambiar a este screen. Se carga el pack de botones,
	 * estilos,...
	 */
	@Override
	public void show() { // this execute first
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));

		load();
	}

	/**
	 * Loads all the textures and sounds, later i will refactor this
	 */
	private void load() {
		try {
			// get font from ouR skin
			font = skin.getFont("black");
			// set boards
			yourBoard = new ViewPort(400, 400, boardSize, boardSize, 220, 130);
			enemyBoard = new ViewPort(400, 400, boardSize, boardSize, 640, 130);
			// <textures>

			// buttons and text
			rotateSprite = new Texture("textures/buttonsAndTexts/rotate.png");
			undoSprite = new Texture("textures/buttonsAndTexts/undo.png");
			musicSprite = new Texture("textures/buttonsAndTexts/music.png");
			musicNoteCancelledSprite = new Texture("textures/buttonsAndTexts/musicCancel.png");
			gameWonSprite = new Texture("textures/buttonsAndTexts/gameWon.png");
			gameLostSprite = new Texture("textures/buttonsAndTexts/gameLost.png");
			backSprite = new Texture("textures/buttonsAndTexts/back.png");
			saveSprite = new Texture("textures/buttonsAndTexts/save.png");
			randomPlacementSprite = new Texture("textures/buttonsAndTexts/randomPlacement.png");
			logoSprite = new Texture("textures/buttonsAndTexts/1.png");
			lineAttackSprite = new Texture("textures/buttonsAndTexts/lineAttack.png");
			AoEAttackSprite = new Texture("textures/buttonsAndTexts/aoeAttack.png");
			// textures
			backgroundSprite = new Texture("textures/backTile.jpg");
			popupSprite = new Texture("textures/popup.png");
			// ships
			waterSprite = new Texture("textures/ships/waterTile.png");
			hitSprite = new Texture("textures/ships/hitTile.png");
			sunkSprite = new Texture("textures/ships/sunkTile.png");
			missSprite = new Texture("textures/ships/missTile.png");
			// normal ships
			shipStartNormalH = new Texture("textures/ships/shipStartNormalH.png");
			shipStartNormalV = new Texture("textures/ships/shipStartNormalV.png");
			shipCenterNormalV = new Texture("textures/ships/shipCenterNormalV.png");
			shipCenterNormalH = new Texture("textures/ships/shipCenterNormalH.png");
			shipEndNormalH = new Texture("textures/ships/shipEndNormalH.png");
			shipEndNormalV = new Texture("textures/ships/shipEndNormalV.png");
			shipSingleNormalH = new Texture("textures/ships/shipSingleNormalH.png");
			shipSingleNormalV = new Texture("textures/ships/shipSingleNormalV.png");
			// shield ships
			shipStartShieldH = new Texture("textures/ships/shipStartShieldH.png");
			shipStartShieldV = new Texture("textures/ships/shipStartShieldV.png");
			shipCenterShieldV = new Texture("textures/ships/shipCenterShieldV.png");
			shipCenterShieldH = new Texture("textures/ships/shipCenterShieldH.png");
			shipEndShieldH = new Texture("textures/ships/shipEndShieldH.png");
			shipEndShieldV = new Texture("textures/ships/shipEndShieldV.png");
			shipSingleShieldH = new Texture("textures/ships/shipSingleShieldH.png");
			shipSingleShieldV = new Texture("textures/ships/shipSingleShieldV.png");
			shieldHit = new Texture("textures/ships/shieldHit.png");
			// </textures>

			// buttons
			rotateButton = new Button(rotateSprite, 164, 440, 32, 32);
			backButton = new Button(backSprite, 830, 60, 83, 16);
			musicButton = new Button(musicSprite, 1060, 450, 32, 32);
			saveButton = new Button(saveSprite, 1060, 350, 32, 32);
			undoButton = new Button(undoSprite, 1060, 250, 32, 32);
			randomPlacementButton = new Button(randomPlacementSprite, 240, 540, 331, 16);
			aoeButton = new Button(AoEAttackSprite, 1160, 350, 64, 64);
			lineAttackButton = new Button(lineAttackSprite, 1160, 250, 100, 64);
			// audio
			music = Gdx.audio.newMusic(Gdx.files.internal("music/Toneless-Handyone.mp3"));
			music.play();
			music.setLooping(true);
			music.setVolume(0.5f);
			explosion = Gdx.audio.newSound(Gdx.files.internal("sound/ExplosionOnWater.mp3"));
			shipPlacer = new ShipPlacer(yourBoard, this, boardSize, gameSize);
			shipPlacer.load();
			// read the board from the game logic
			readBoard(yourBoard, true);
			readBoard(enemyBoard, false);
			Gdx.input.setInputProcessor(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is called each FPS. It render font, buttons, board, pop up panels, status
	 * bar.
	 * 
	 * @param delta Delta time is how many seconds have passed since the last render
	 *              call.
	 */
	private void renderStuff(float delta) {
		// Set clear and set background color
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		// draw stuff
		batch = game.getBatch();
		batch.begin();
		// draw background
//		this.renderBackground();
		// draw font
		if (statusBar != null)
			font.draw(batch, statusBar, 228, 120);
		if (saveBar != null)
			font.draw(batch, saveBar, 50, 30);
		if (pointsBar != null)
			font.draw(batch, pointsBar, 240, 560);
		if (enemyIdBar != null)
			font.draw(batch, enemyIdBar, 650, 560);
		// draw buttons
		batch.draw(logoSprite, 400, 590, 483, 43);
		backButton.render(batch);
		if (controller.getState() == GameState.PLACE) {
			shipPlacer.hoverPlacingShip();
			rotateButton.render(batch);
			randomPlacementButton.render(batch);
		} else {
			readBoard(yourBoard, true);
			readBoard(enemyBoard, false);
		}

		// draw boards
		musicButton.render(batch);
		yourBoard.render(batch, waterSprite);
		enemyBoard.render(batch, waterSprite);
		if (controller.getState() == GameState.WON || controller.getState() == GameState.LOSE) {
			if (controller.getState() == GameState.WON) {
				this.renderWon();
			}
			if (controller.getState() == GameState.LOSE) {
				this.renderLost();
			}
		}

		if (controller.getState() == GameState.PLAY && GameMode.PVE == gameMode) {
			undoButton.render(batch);
			saveButton.render(batch);
			aoeButton.render(batch);
			lineAttackButton.render(batch);
		}
		batch.end();
	}

	/**
	 * This render function is called by libGDX each FPS. <br>
	 * We also use it to call Bot attack action.
	 */
	@Override
	public void render(float delta) {
		if (gameMode == GameMode.PVE)
			botFire();
		renderStuff(delta);
		this.updateStatusBar();
	}

	private void updateStatusBar() {
		if (controller.getState() == GameState.PLACE)
			statusBar = "[place your ships]";
		else if (controller.getState() == GameState.WAIT_PLACE)
			statusBar = "[Waiting enemy to place the ships]";
		else if (controller.getState() == GameState.PLAY)
			statusBar = "[Play]";
		else if (controller.getState() == GameState.WAIT_ATTACK)
			statusBar = "[Wait enemy attack]";
		else if (controller.getState() == GameState.LOST_CONNECTION)
			statusBar = "[Lost connection with the enemy]";
		else if (controller.getState() == GameState.WON)
			statusBar = "[You won]";
		else if (controller.getState() == GameState.LOSE)
			statusBar = "[You lose]";
		else if (controller.getState() == GameState.ENEMY_EXIT)
			statusBar = "[Enemy exit the battle. You win]";
		else
			statusBar = null;
		if (controller.getState() != GameState.PLACE && controller.getState() != GameState.WON
				&& controller.getState() != GameState.LOSE)
			pointsBar = String.valueOf(controller.getPoints());
		else
			pointsBar = null;
	}

	/**
	 * Read the board from the model through controller <br>
	 * <br>
	 * Codification in model = [position][type][orientation][shield] <br>
	 * <br>
	 * position = [S]tart-[C]enter-[E]nd <br>
	 * type = [S]ingle-[N]ormal <br>
	 * orientation = [H]orizontal-[V]ertical <br>
	 * shield = [1]-On, [0]-Off<br>
	 * 
	 * @param board  ViewPort used as the board
	 * @param player If is player is True
	 */
	public void readBoard(ViewPort board, boolean player) {
		Cell matrix[][];
		if (player)
			matrix = controller.getPlayerBoard();
		else
			matrix = controller.getEnemyBoard();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (matrix[i][j].get().equals(Cell.WATER_CELL)) {
					board.setCell(i, j, waterSprite);
				} else if (matrix[i][j].get().equals(Cell.HITTED_CELL)) {
					board.setCell(i, j, hitSprite);
				} else if (matrix[i][j].get().equals(Cell.KILLED_CELL)) {
					board.setCell(i, j, sunkSprite);
				} else if (matrix[i][j].get().equals(Cell.MISSED_CELL)) {
					board.setCell(i, j, missSprite);
				} else if (matrix[i][j].get().equals(null)) {
					board.setCell(i, j, waterSprite);
				} else if (player) {// hacer que sea siempre true, para que sea como antes cambiar a 'player'
					if (matrix[i][j].get().charAt(1) == 'S') {
						if (matrix[i][j].get().charAt(2) == 'H') {
							if (matrix[i][j].get().charAt(3) == '1')
								board.setCell(i, j, shipSingleShieldH);
							else
								board.setCell(i, j, shipSingleNormalH);
						} else {
							if (matrix[i][j].get().charAt(3) == '1')
								board.setCell(i, j, shipSingleShieldV);
							else
								board.setCell(i, j, shipSingleNormalV);
						}
					} else {
						if (matrix[i][j].get().charAt(2) == 'H') {
							if (matrix[i][j].get().charAt(3) == '1') {
								if (matrix[i][j].get().charAt(0) == 'S')
									board.setCell(i, j, shipStartShieldH);
								else if (matrix[i][j].get().charAt(0) == 'C')
									board.setCell(i, j, shipCenterShieldH);
								else
									board.setCell(i, j, shipEndShieldH);

							} else {
								if (matrix[i][j].get().charAt(0) == 'S')
									board.setCell(i, j, shipStartNormalH);
								else if (matrix[i][j].get().charAt(0) == 'C')
									board.setCell(i, j, shipCenterNormalH);
								else
									board.setCell(i, j, shipEndNormalH);
							}
						} else {
							if (matrix[i][j].get().charAt(3) == '1') {
								if (matrix[i][j].get().charAt(0) == 'S')
									board.setCell(i, j, shipStartShieldV);
								else if (matrix[i][j].get().charAt(0) == 'C')
									board.setCell(i, j, shipCenterShieldV);
								else
									board.setCell(i, j, shipEndShieldV);

							} else {
								if (matrix[i][j].get().charAt(0) == 'S')
									board.setCell(i, j, shipStartNormalV);
								else if (matrix[i][j].get().charAt(0) == 'C')
									board.setCell(i, j, shipCenterNormalV);
								else
									board.setCell(i, j, shipEndNormalV);
							}
						}
					}
				} else {
					// esta linea rellena el tablero enemigo de agua
					board.setCell(i, j, waterSprite);
				}
			}
		}

	}

	/**
	 * Render win
	 */
	private void renderWon() {
		statusBar = "you win!";
//		batch.draw(popupSprite, 168, 38, 8, 8);
//		batch.draw(gameWonSprite, 250, 350);
	}

	/**
	 * Render lost
	 */
	private void renderLost() {
		statusBar = "you lose!";
//		batch.draw(popupSprite, 168, 38, 8, 8);
//		batch.draw(gameLostSprite, 250, 350);
	}

	private void renderBackground() {
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		int backWidth = backgroundSprite.getWidth(), backHeight = backgroundSprite.getHeight();
		for (int i = 0; i < width; i = i + backWidth) {
			for (int j = 0; j < height; j = j + backHeight) {
				batch.draw(backgroundSprite, i, j);
			}
		}
	}

	// ---------------- Response to different human actions ----------------

	/**
	 * If typed 'space key' during placing, switch orientation of the ship
	 */
	@Override
	public boolean keyTyped(char character) {
		if (character == ' ') {
			if (controller.getState() == GameState.PLACE) {
				shipPlacer.switchOrientation();
			}
		}
		return false;
	}

	/**
	 * Is called when player clicked on the screen
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		saveBar = null;
		if (button == Buttons.LEFT) {
			Gdx.app.log(HundirLaFlota.TITLE, "Click pos: " + screenX + " " + (720 - screenY));
			if (yourBoard.mouseInGrid(screenX, screenY)) {
				if (controller.getState() == GameState.PLACE) {
					shipPlacer.placeShip();
				} else if (controller.getState() == GameState.PLAY)
					System.out.println("This is your table, YOU LITTLE BASTARD");
			} else if (enemyBoard.mouseInGrid(screenX, screenY)) {
				if (controller.getState() == GameState.PLAY) {
					int[] location = enemyBoard.returnMouseLocation(screenX, screenY);
					try {
						GameResponse response;
						response = controller.fire(location[0], location[1], this.fireType, true);
						if ((response == GameResponse.HIT || response == GameResponse.SUNK) && playSound) {
							explosion.play();
						} else if (response == GameResponse.INVALID) {
							// Do nothing if trying to shoot a space that has been hit before
						} else if (response == GameResponse.WON) {
							this.win();
						} else if (response == GameResponse.SHIELD) {
							// play shield destroyed sound
//							enemyBoard.setCell(location[0], location[1], shieldHit);
						}
						fireType = 0;
						readBoard(yourBoard, true);
						readBoard(enemyBoard, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// music
			else if (musicButton.mouseOverButton(screenX, screenY)) {
				if (playSound) {
					music.pause();
					playSound = false;
					musicButton.setImage(musicNoteCancelledSprite);
				} else {
					music.play();
					playSound = true;
					musicButton.setImage(musicSprite);
				}

			}
			// rotate
			else if (rotateButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLACE) {
					shipPlacer.switchOrientation();
				}

			}
			// back
			else if (backButton.mouseOverButton(screenX, screenY)) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game));
			}
			// undo
			else if (undoButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLAY) {
					Gdx.app.log(HundirLaFlota.TITLE, "Called memento load");
					((LocalController) controller).undo();
					readBoard(yourBoard, true);
					readBoard(enemyBoard, false);
				}
			}
			// save
			else if (saveButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLAY && gameMode == GameMode.PVE) {
					Gdx.app.log(HundirLaFlota.TITLE, "Called save");
					try {
						((LocalController) controller).save();
					} catch (IOException e) {
						Gdx.app.log(HundirLaFlota.TITLE, "Caught error during save");
						saveBar = "error during save the game";
					}
					saveBar = "Game saved in 'Client/desktop/savedGames/savedGame.dat'";
					readBoard(yourBoard, true);
					readBoard(enemyBoard, false);
				}
			}
			// randomPlacement
			else if (randomPlacementButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLACE) {
					Gdx.app.log(HundirLaFlota.TITLE, "Called random placement");
					controller.randomPlacement();
					placementFinished();
					readBoard(yourBoard, true);
					readBoard(enemyBoard, false);
				}
			}
			// AoEAttack
			else if (aoeButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLAY) {
					if (controller.buyAttack(2)) {
						saveBar = "Bought AoEAttack";
						Gdx.app.log(HundirLaFlota.TITLE, "Bought AoEAttack");
						onAOEAttackClicked();
					} else {
						saveBar = "insufficient points. You need 4";
					}
				}
			}
			// lineAttack
			else if (lineAttackButton.mouseOverButton(screenX, screenY)) {
				if (controller.getState() == GameState.PLAY) {
					if (controller.buyAttack(1)) {
						saveBar = "Bought lineAttack";
						Gdx.app.log(HundirLaFlota.TITLE, "Bought lineAttack");
						onLineAttackClicked();
					} else {
						saveBar = "insufficient points. you need 6";
					}
				}
			}
		}
		// right click on screen
		else if (button == Buttons.RIGHT) {
			if (controller.getState() == GameState.PLACE) {
				shipPlacer.switchOrientation();
			} else if (controller.getState() == GameState.PLAY && gameMode == GameMode.PVP) {
				Gdx.app.log(HundirLaFlota.TITLE, "Right click on pos: " + screenX + " " + screenY);
				if (yourBoard.mouseInGrid(screenX, screenY)) {
					int[] location = yourBoard.returnMouseLocation(screenX, screenY);
					try {
						Gdx.app.log(HundirLaFlota.TITLE, "Shield called");
						controller.setShield(location[0], location[1], 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	/**
	 * Change type of attack to lineAttack
	 */
	public void onLineAttackClicked() {
		this.fireType = 1;
	}

	/**
	 * Change type of attack to AOEAttack
	 */
	public void onAOEAttackClicked() {
		this.fireType = 2;
	}

	/**
	 * Used in PvE mode to manage AI fire after player shot
	 */
	private void botFire() {
		GameResponse response;
		if (controller.getState() == GameState.PLAY)
			try {
				response = controller.autoFire(-1, -1, 0, false);
				if ((response == GameResponse.HIT || response == GameResponse.SUNK) && playSound) {
					explosion.play();
				} else if (response == GameResponse.WON) {
					this.lose();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/*
	 * Manage the finish of placement an the start of the game
	 */
	public void placementFinished() {
		controller.setState(GameState.PLAY);
		controller.setPlacementAsFinished();
		shipPlacer.dispose();
	}

	/**
	 * Manage the lose
	 */
	private void lose() {
		controller.setState(GameState.LOSE);
	}

	/**
	 * Manage the win
	 */
	private void win() {
		controller.setState(GameState.WON);
	}

	public Controller getController() {
		return controller;
	}

	@Override
	public boolean keyDown(int keycode) {
		// Do nothing
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Do nothing
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	/**
	 * Used when we hide the screen. Example: Set other screen
	 */
	@Override
	public void hide() {
		this.dispose();
	}

	/**
	 * Dispose method. Used to dispose textures
	 */
	@Override
	public void dispose() {
		music.dispose();
		explosion.dispose();
		skin.dispose();
		music.dispose();
		waterSprite.dispose();
		hitSprite.dispose();
		sunkSprite.dispose();
		missSprite.dispose();
		backgroundSprite.dispose();
		logoSprite.dispose();
		musicSprite.dispose();
		musicNoteCancelledSprite.dispose();
		rotateSprite.dispose();
		popupSprite.dispose();
		gameWonSprite.dispose();
		gameLostSprite.dispose();
		backSprite.dispose();
		undoSprite.dispose();
		saveSprite.dispose();
		randomPlacementSprite.dispose();
		shipStartNormalH.dispose();
		shipStartNormalV.dispose();
		shipCenterNormalH.dispose();
		shipCenterNormalV.dispose();
		shipEndNormalH.dispose();
		shipEndNormalV.dispose();
		shipSingleNormalV.dispose();
		shipSingleNormalH.dispose();
		shipStartShieldH.dispose();
		shipStartShieldV.dispose();
		shipCenterShieldH.dispose();
		shipCenterShieldV.dispose();
		shipEndShieldH.dispose();
		shipEndShieldV.dispose();
		shipSingleShieldH.dispose();
		shipSingleShieldV.dispose();
		shieldHit.dispose();
		yourBoard.dispose();
		enemyBoard.dispose();
	}

	@Override
	public void onEnd(AnimationDesc animation) {
	}

	@Override
	public void onLoop(AnimationDesc animation) {
	}

}
