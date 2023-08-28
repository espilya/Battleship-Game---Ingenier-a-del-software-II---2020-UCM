package gui.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import gui.game.HundirLaFlota;
import simulator.controller.LocalController;
import simulator.model.BotDifficulty;
import simulator.model.GameMode;
import simulator.model.GameSize;

/**
 * Class used to represent the PvE selector and load the game.
 *
 */
public class LevelMenuScreen extends ScreenAdapter {

	private Stage stage;
	private Skin skin;
	private TextButton buttonPlay, buttonBack, buttonLoad;
	private HundirLaFlota game;
	private ScrollPane scrollPaneDifficulty, scrollPaneMode, scrollPaneSize;
	private TextField levelDirectoryInput;
	private GameMode gameMode;

	public LevelMenuScreen(HundirLaFlota game) {
		this.game = game;
		gameMode = GameMode.PVE;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (gameMode == GameMode.PVE) {
			scrollPaneDifficulty.setVisible(true);
			levelDirectoryInput.setVisible(true);
		} else {
			scrollPaneDifficulty.setVisible(false);
			levelDirectoryInput.setVisible(false);
		}
		stage.act(delta);
		stage.draw();
		stage.setDebugAll(MainMenuScreen.DEBUG_TABLES);
	}

	@Override
	public void show() {
		Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("GameSize", GameSize.REDUCED.toString());
		Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("GameMode", GameMode.PVE.toString());
		Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("BotDifficulty", BotDifficulty.EASY.toString());
		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		buildStage();
	}

	private void buildStage() {
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));
		// build all layers
		Table layerMenu = buildMenu();
		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		stack.add(layerMenu);
	}

	private void onPlayClicked() {
		// save the settings to preferences file (Preferences#flush() writes the
		// preferences in memory to the file)
		Gdx.app.getPreferences(HundirLaFlota.TITLE).flush();
		Gdx.app.log(HundirLaFlota.TITLE, "settings saved");
		((Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen(game));
	}

	private void onBackClicked() {
		((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game));
	}

	private void onLoadClicked() {
		String levelFile = levelDirectoryInput.getText().trim();
		System.out.println("level directory " + levelFile);
		LocalController controller = new LocalController();
		if (controller.load(levelFile)) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen(game, controller));
		}
	}

	private Table buildMenu() {
		// Creating lists with elements & Creating scrollPanel (mode, size, and
		// difficulty selection)
		List<String> listDifficulty = new List<String>(skin);
		listDifficulty.setItems(BotDifficulty.getNames());
		List<String> listMode = new List<String>(skin);
		listMode.setItems(new String[] { "PVE" });
//		listMode.setItems(GameMode.getNames());
		List<String> listSize = new List<String>(skin);
		listSize.setItems(GameSize.getNames());

		// Creating scrollPanels with lists
		scrollPaneDifficulty = new ScrollPane(listDifficulty, skin);
		scrollPaneMode = new ScrollPane(listMode, skin);
		scrollPaneSize = new ScrollPane(listSize, skin);

		// Adding listeners to scrollPanels
		scrollPaneDifficulty.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				String val = (String) ((List<?>) event.getTarget()).getSelection().getLastSelected();
				Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("BotDifficulty", val);
				System.out.println(val);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		scrollPaneMode.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				String val = (String) ((List<?>) event.getTarget()).getSelection().getLastSelected();
				gameMode = GameMode.valueOf(val);
				Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("GameMode", val);
				System.out.println(val);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		scrollPaneSize.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				String val = (String) ((List<?>) event.getTarget()).getSelection().getLastSelected();
				Gdx.app.getPreferences(HundirLaFlota.TITLE).putString("GameSize", val);
				System.out.println(val);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		// Creating buttons
		buttonPlay = new TextButton("Play", skin);
		buttonPlay.pad(10);
		buttonBack = new TextButton("Back", skin);
		buttonBack.pad(10);
		buttonLoad = new TextButton("Load", skin);
		buttonLoad.pad(10);
		// Adding listeners to Buttons
		buttonPlay.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onPlayClicked();
			}
		});
		buttonBack.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onBackClicked();
			}
		});
		buttonLoad.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onLoadClicked();
			}
		});
		// creating textField
		levelDirectoryInput = new TextField("savedGame.dat", skin);
		levelDirectoryInput.setMessageText("saved game name"); // set the text to be shown when nothing is in the

		// Adding stuff to the table
		Table table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		table.padTop(400);
//		table.add().width(table.getWidth() / 3);
		table.add("Select level").colspan(3).expandX().spaceBottom(50).row();
		table.add().width(table.getWidth() / 3);
		table.add().width(table.getWidth() / 3);
		table.add().width(table.getWidth() / 3);
		table.row();
		table.add(scrollPaneMode);
		table.add(buttonPlay);
		table.add(scrollPaneSize);
		table.row();
		table.add(scrollPaneDifficulty);
		table.add(buttonBack);
		table.add(levelDirectoryInput).fillX().row();
		table.add();
		table.add();
		table.add(buttonLoad);
		table.row();
		table.add().width(table.getWidth() / 3);
		table.add().width(table.getWidth() / 3);
//		table.debug(); // Remove
		return table;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
//		menuTable.invalidateHierarchy();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		stage.dispose();
		skin.dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

}
