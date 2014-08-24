package tk.coaster3000.worldinfo;

import tk.coaster3000.worldinfo.bukkit.WorldInfoPlugin;

import java.util.Locale;

public class Settings {

	private static final String CHANNEL_NODE = "channel";
	private static final String ENCODING_NODE = "encoding";
	private static final String ID_MODE_NODE = "mode";
	private static final String INFORM_PLAYER_NODE = "inform-player";
	private static final String PLUGIN_CHANNEL = "world_info";

	private String encoding = "UTF-8";
	private ID_MODE id_mode = ID_MODE.NAME;

	private ConfigProvider config;
	private final WorldInfo instance;
	private boolean inform_player;

	public Settings(WorldInfo instance, ConfigProvider config) {
		this.instance = instance;
		this.config = config;
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

		config.save();
		return this;
	}

	public Settings load() {
		config.load();

		inform_player(config.getBoolean("inform-player", false));
		encoding(config.getString("encoding", "UTF-8"));
		id_mode(ID_MODE.valueOf(config.getString("mode", ID_MODE.NAME.name()).toUpperCase(Locale.ENGLISH)));

		return this;
	}
}
