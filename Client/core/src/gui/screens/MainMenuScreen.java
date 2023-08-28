package gui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import gui.game.HundirLaFlota;

public class MainMenuScreen implements Screen {

	public final static boolean DEBUG_TABLES = false;

	private Stage stage;
	private Skin skin;
	private Table table;
	private TextButton buttonPlay, buttonMultiplayer, buttonPref, buttonExit, buttonScore;
	private BitmapFont white, black;
	private Label heading;
	private HundirLaFlota game;

	public MainMenuScreen(HundirLaFlota game) {
		this.game = game;
	}

	@Override
	public void show() {
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
//		table.setFillParent(true);

		// create heading
		heading = new Label(HundirLaFlota.TITLE, skin);
		heading.setFontScale(2);

		// creating buttons
		buildButtons();

		// putting stuff in the table
		table.padTop(400);
		table.add(heading);
		table.getCell(heading).spaceBottom(60);
		table.row();
		// table.padBottom(50);
		table.add(buttonPlay).spaceBottom(15).row();
		table.add(buttonMultiplayer).spaceBottom(15).row();
		table.add(buttonScore).spaceBottom(15).row();
		table.add(buttonPref).spaceBottom(15).row();
		table.add(buttonExit).spaceBottom(15).row();
//		table.debug(); // Remove
		stage.addActor(table);
	}

	// public MainMenuScreen() {
	// System.out.print("aaaa");
	// myWorld = new GameWorld();
	// cam = new OrthographicCamera();
	// cam.setToOrtho(true, 136, 204);
	// shapeRenderer = new ShapeRenderer();
	// shapeRenderer.setProjectionMatrix(cam.combined);
	// }

	private void buildButtons() {

		// creating buttons
		buttonPlay = new TextButton("Play", skin);
		buttonPlay.pad(10);
		buttonPlay.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenuScreen(game));
			}
		});

		buttonMultiplayer = new TextButton("Multiplayer", skin);
		buttonMultiplayer.pad(10);
		buttonMultiplayer.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new LobbyScreen(game));
			}
		});

		buttonExit = new TextButton("Exit", skin);
		buttonExit.pad(10);
		buttonExit.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		buttonPref = new TextButton("Settings", skin);
		buttonPref.pad(10);
		buttonPref.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen(game));
			}
		});

		buttonScore = new TextButton("Score List", skin);
		buttonScore.pad(10);
		buttonScore.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
//				Gdx.app.exit();
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
		stage.setDebugAll(DEBUG_TABLES);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		table.invalidateHierarchy();
//		stage.setViewport(width, height, false);
//		table.setClip(true);
//		table.setSize(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
//		dispose(); posiblemente hace falta, pero a mi me salta error en determinadas ocasiones
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		white.dispose();
		black.dispose();
	}

}

///*
//* 2. We draw the Filled rectangle
//*/
//
//// Tells shapeRenderer to begin drawing filled shapes
//shapeRenderer.begin(ShapeType.Filled);
//
//// Chooses RGB Color of 87, 109, 120 at full opacity
//shapeRenderer.setColor(87 / 255.0f, 109 / 255.0f, 120 / 255.0f, 1);
//
//// Draws the rectangle from myWorld (Using ShapeType.Filled)
//shapeRenderer.rect(myWorld.getRect().x, myWorld.getRect().y,
//       myWorld.getRect().width, myWorld.getRect().height);
//
//// Tells the shapeRenderer to finish rendering
//// We MUST do this every time.
//shapeRenderer.end();
//
///*
//* 3. We draw the rectangle's outline
//*/
//
//// Tells shapeRenderer to draw an outline of the following shapes
//shapeRenderer.begin(ShapeType.Line);
//
//// Chooses RGB Color of 255, 109, 120 at full opacity
//shapeRenderer.setColor(255 / 255.0f, 109 / 255.0f, 120 / 255.0f, 1);
//
//// Draws the rectangle from myWorld (Using ShapeType.Line)
//shapeRenderer.rect(myWorld.getRect().x, myWorld.getRect().y,
//       myWorld.getRect().width, myWorld.getRect().height);
//
//shapeRenderer.end();
//
//// Covert Frame rate to String, print it
//Gdx.app.log("GameScreen FPS", (1/delta) + "");	