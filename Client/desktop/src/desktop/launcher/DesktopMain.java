package desktop.launcher;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import gui.game.HundirLaFlota;


 
/**
 * Class used to launch the desktop version of the game.
 *
 */
public class DesktopMain {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = HundirLaFlota.TITLE + " v" + HundirLaFlota.VERSION;
		config.width = 1280;
		config.height = 720;
		config.resizable = false;
		config.vSyncEnabled = true;
//		config.addIcon("img/icon.png", FileType.Internal); //TODO
		new LwjglApplication(new HundirLaFlota(), config);
	}
}
