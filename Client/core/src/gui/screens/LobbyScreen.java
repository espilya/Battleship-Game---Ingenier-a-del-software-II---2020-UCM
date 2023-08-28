package gui.screens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONTokener;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import gui.game.HundirLaFlota;
import simulator.controller.GuestController;
import simulator.controller.HostController;
import simulator.controller.LocalController;
import simulator.model.GameSize;
import utils.Host;

public class LobbyScreen implements Screen {
	private static String newLine = System.getProperty("line.separator");

	private HundirLaFlota game;
	private Stage stage;
	private Skin skin;
	private TextButton buttonConnect;
	private Table mainScreen, bottomButtons, topButtons, createServer, errorPanel, idAskPanel;

	// servers
	private java.util.List<Host> hostsList;
	private List<String> listItems;
	private ScrollPane serverList;
	private Host selectedHost;
	private Socket socket;
	private String id;

	private boolean lobbyMenuVisible;

	public LobbyScreen(HundirLaFlota game) {
		this.game = game;
		hostsList = new ArrayList<Host>();
		selectedHost = null;
		lobbyMenuVisible = true;
//		connectWithMainServer();
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), new TextureAtlas("ui/atlas.pack"));
		buildStage();
		mainScreen.setVisible(false);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		buttonConnect.setVisible(selectedHost != null);
//		updateList();
		stage.act(delta);
		stage.draw();
		stage.setDebugAll(MainMenuScreen.DEBUG_TABLES);
	}

	private void onChangeMenusClicked() {
		lobbyMenuVisible = !lobbyMenuVisible;
		mainScreen.setVisible(lobbyMenuVisible);
		createServer.setVisible(!lobbyMenuVisible);
	}

	private void onUpdateClicked() {
		updateList();
	}

	private void onStartServerClicked(GameSize size) {
		try {
			socket.close();
		} catch (Exception e) {
		}
		((Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen(game, new HostController(size, id)));
	}

	private void onConectClicked() {
		try {
			socket.close();
			((Game) Gdx.app.getApplicationListener()).setScreen(new BattleScreen(game,
					new GuestController(GameSize.valueOf(selectedHost.getSize()), selectedHost.getSocket(), id)));
		} catch (Exception e) {
			showErrorPanel();
		}
	}

	private void onBackClicked() {
		((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(game));
	}

	private void askListFromServer() {
		String jsonList;
		try {
			connectWithMainServer();
			BufferedWriter writerChannel = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			BufferedReader readerChannel = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writerChannel.write(2 + newLine);
			writerChannel.flush();
			jsonList = readerChannel.readLine();
			readStringJSON(jsonList);
		} catch (Exception e) {
			showErrorPanel();
		}
	}

	private void readStringJSON(String jsonList) {
		JSONTokener tokener = new JSONTokener(jsonList);
		JSONArray jA = new JSONArray(tokener);
		hostsList.clear();
		for (int i = 0; i < jA.length(); i++) {
			hostsList.add(new Host(jA.getJSONObject(i)));
		}
	}

	private void updateList() {
		askListFromServer();
		listItems = new List<String>(skin);
		listItems.getItems().clear();
		for (int i = 0; i < hostsList.size(); i++) {
			Host h = hostsList.get(i);
			listItems.getItems().add(i + " - " + h.getSize() + " - " + h.getId());
		}
		serverList.setActor(listItems);
	}

	private void buildStage() {
		Stack stack = new Stack();
		bottomButtons = buildBottomButtons();
		topButtons = buildTopButtons();
		createServer = buildCreateServerMenu();
		errorPanel = buildConnectionErrorPanel();
		mainScreen = buildMenu();
		idAskPanel = buildIdAskPanel();
		errorPanel.setVisible(false);
		mainScreen.setVisible(lobbyMenuVisible);
		createServer.setVisible(!lobbyMenuVisible);
		stack.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		stack.add(mainScreen);
		stack.add(createServer);
		stack.add(errorPanel);
		stack.add(idAskPanel);
		stage.clear();
		stage.addActor(stack);
	}

	private Table buildIdAskPanel() {
		// create stuff
		Table table = new Table();
		Table buttons = new Table();
		Label label = new Label("Write you id and press next", skin);
		TextButton back = new TextButton("back", skin);
		TextButton next = new TextButton("next", skin);
		TextField idInput = new TextField("", skin);
		idInput.setMessageText("your_id_here");
		// crate listeners for buttons
		back.pad(10);
		back.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onBackClicked();
			}
		});
		next.pad(10);
		next.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if ((idInput.getText() != "" && !idInput.getText().trim().isEmpty())) {
					Gdx.app.log(HundirLaFlota.TITLE, "id: " + id);
					id = idInput.getText().trim();
					mainScreen.setVisible(true);
					idAskPanel.setVisible(false);
				}
			}
		});
		buttons.add(back);
		buttons.add().width(250);
		buttons.add(next);
		// setup stuff
		label.setFontScale(1.2f);

		// add stuff to main table
		table.add(label).spaceBottom(60).row();
		table.add(idInput).width(600).fillX().space(35).row();
		table.add(buttons).expandX();

		// setup main table
		table.setFillParent(true);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		table.padTop(600);
		table.center();
		table.padTop(400);
		return table;
	}

	private Table buildConnectionErrorPanel() {
		// create stuff
		Table table = new Table();
		Label label = new Label("Error connecting to server. Please try again later.", skin);
		TextButton back = new TextButton("back", skin);

		// crate listeners for buttons
		back.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				errorPanel.setVisible(false);
				mainScreen.setVisible(true);
				createServer.setVisible(false);
			}
		});

		// setup stuff
//		label.setFontScale(1.5f);
		back.pad(5);

		// add stuff to main table
		table.add(label).spaceBottom(60).row();
		table.add(back).pad(10);

		// setup main table
		table.setFillParent(true);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		table.padTop(600);
		table.center();
		table.padTop(400);
		return table;
	}

	private Table buildMenu() {
		Table table = new Table();
		Label label = new Label("Servers List", skin);
		serverList = buildScrollList();
		listItems = new List<String>(skin);
		String[] a = {};
		listItems.setItems(a);
		serverList.setActor(listItems);
		label.setFontScale(1.5f);

		table.add(label).spaceBottom(20).row();
		table.add(topButtons).expandX();
		table.add().row();
		table.add(serverList).space(15).expandX().height(350).minWidth(800);
		table.row();
		table.add(bottomButtons).expandX();

		table.setFillParent(true);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		table.padTop(550);
		table.center();
		return table;
	}

	private Table buildCreateServerMenu() {

		// create stuff
		Table table = new Table();
		Table buttonsPanel = new Table();
		Label label = new Label("Select game size", skin);
		TextButton reduced = new TextButton("reduced", skin);
		TextButton classic = new TextButton("classic", skin);
		TextButton insane = new TextButton("insane", skin);
		TextButton cancel = new TextButton("cancel", skin);

		// crate listeners for buttons
		reduced.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onStartServerClicked(GameSize.REDUCED);
			}
		});
		classic.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onStartServerClicked(GameSize.CLASSIC);
			}
		});
		insane.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onStartServerClicked(GameSize.INSANE);
			}
		});
		cancel.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onChangeMenusClicked();
			}
		});

		// setup stuff
		label.setFontScale(1.5f);
		reduced.pad(5);
		classic.pad(5);
		insane.pad(5);
		cancel.pad(5);

		// add buttons to Panel for buttons
		buttonsPanel.add(reduced).pad(10).fillX();
		buttonsPanel.add(classic).pad(10).fillX();
		buttonsPanel.add(insane).pad(10).fillX();

		// add stuff to main table
		table.add(label).spaceBottom(60).row();
		table.add(buttonsPanel).spaceBottom(30).row();
		table.add(cancel);

		// setup main table
		table.setFillParent(true);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		table.padTop(600);
		table.center();
		table.padTop(400);
		return table;
	}

	private ScrollPane buildScrollList() {
		ScrollPane pane = new ScrollPane(listItems, skin);
		pane.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				String val = (String) ((List<?>) event.getTarget()).getSelection().getLastSelected();
				if (!hostsList.isEmpty() && val != null) {
					selectedHost = hostsList.get(Integer.valueOf(val.charAt(0)) - 48);
					System.out.println("Selected host: id=" + selectedHost.getId() + " size=" + selectedHost.getSize()
							+ " socket=" + selectedHost.getSocket());
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		return pane;
	}

	private Table buildTopButtons() {

		TextButton buttonCreate = new TextButton("Create Game", skin);
		buttonCreate.pad(15);
		buttonCreate.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onChangeMenusClicked();
			}
		});

		TextButton buttonUpdate = new TextButton("Update list", skin);
		buttonUpdate.pad(15);
		buttonUpdate.padLeft(40);
		buttonUpdate.padRight(40);
		buttonUpdate.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onUpdateClicked();
			}
		});

		Table buttonsTable = new Table();
		buttonsTable.add(buttonCreate);
		buttonsTable.add().width(250);
		buttonsTable.add(buttonUpdate);
		return buttonsTable;
	}

	private Table buildBottomButtons() {

		TextButton buttonBack = new TextButton("Back", skin);
		buttonBack.pad(15);
		buttonBack.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onBackClicked();
			}
		});

		buttonConnect = new TextButton("Connect", skin);
		buttonConnect.pad(15);
		buttonConnect.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onConectClicked();
			}
		});

		Table buttonsTable = new Table();
		buttonsTable.add(buttonBack);
		buttonsTable.add().width(250);
		buttonsTable.add(buttonConnect);
		return buttonsTable;
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
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

	private void showErrorPanel() {
		errorPanel.setVisible(true);
		mainScreen.setVisible(false);
		createServer.setVisible(false);
	}

	private void connectWithMainServer() {
		try {
			socket = new Socket("127.0.0.1", 12345);
		} catch (Exception e) {
			showErrorPanel();
		}
	}

}
