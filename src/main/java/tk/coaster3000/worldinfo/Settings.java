package tk.coaster3000.worldinfo;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import tk.coaster3000.worldinfo.bukkit.WorldInfoPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {

	private static final String CHANNEL_NODE = "channel";
	private static final String ENCODING_NODE = "encoding";
	private static final String ID_MODE_NODE = "mode";
	private static final String INFORM_PLAYER_NODE = "inform-player";
	private static final String PLUGIN_CHANNEL = "world_info";

	private String encoding = "UTF-8";
	private ID_MODE id_mode = ID_MODE.NAME;

	private FileConfiguration config;
	private File configFile;
	private final Logger log;
	private final WorldInfoPlugin plugin;
	private boolean inform_player;

	public Settings(WorldInfoPlugin plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.configFile = new File(plugin.getDataFolder(), "config.yml");
		this.config = plugin.getConfig();
	}

	public static Settings getInstance() {
		return WorldInfoPlugin.getInstance().getSettings();
	}

	public static enum ID_MODE {
		NAME, UUID, FILE;
	}

	public static String channel() {
		return PLUGIN_CHANNEL;
	}

	public static String encoding() {
		return getInstance().encoding;
	}

	public Settings encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public static ID_MODE id_mode() {
		return getInstance().id_mode;
	}

	public Settings id_mode(ID_MODE mode) {
		this.id_mode = mode;
		return this;
	}

	public Settings inform_player(boolean inform) {
		this.inform_player = inform;
		return this;
	}

	public static boolean inform_player() {
		return getInstance().inform_player;
	}

	public Settings save() {

		config.set(INFORM_PLAYER_NODE, inform_player());
		config.set(CHANNEL_NODE, channel());
		config.set(ENCODING_NODE, encoding());
		config.set(ID_MODE_NODE, id_mode());

		try {
			config.save(configFile);
		} catch (IOException e) {
			log.log(Level.WARNING, "Failed to save file...", e);
		}
		return this;
	}

	public Settings load() {
		try {
			config.load(configFile); //Reload config automatically.
		} catch (IOException e) {
			log.log(Level.WARNING, "IO Exception Occured... Could not load config..", e);
		} catch (InvalidConfigurationException e) {
			log.log(Level.SEVERE, "Invalid configuration file supplied. Did you put tabs in it?!", e);
		}

		inform_player(config.getBoolean("inform-player", false));
		encoding(config.getString("encoding", "UTF-8"));
		id_mode(ID_MODE.valueOf(config.getString("mode", ID_MODE.NAME.name()).toUpperCase(Locale.ENGLISH)));

		return this;
	}
}
