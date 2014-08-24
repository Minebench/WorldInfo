package tk.coaster3000.worldinfo.bukkit;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import tk.coaster3000.worldinfo.ConfigProvider;

import java.io.File;
import java.io.IOException;

public class BukkitFileConfigProvider extends ConfigProvider {
	private FileConfiguration config;
	private File configFile;

	private BukkitFileConfigProvider(FileConfiguration config, File configFile) {
		this.config = config;
		this.configFile = configFile;
	}

	public static BukkitFileConfigProvider wrap(Plugin plugin) {
		return new BukkitFileConfigProvider(plugin.getConfig(), new File(plugin.getDataFolder(), "config.yml"));
	}

	@Override
	public long getLong(String path, long def) {
		return config.getLong(path, def);
	}

	@Override
	public int getInt(String path, int def) {
		return config.getInt(path, def);
	}

	@Override
	public String getString(String path, String def) {
		return config.getString(path, def);
	}

	@Override
	public void set(String path, Object value) {
		config.set(path, value);
	}

	@Override
	public File getConfigFile() {
		return configFile;
	}

	@Override
	public void save(File file) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace(); //TODO: Logger this
		}
	}

	@Override
	public void load(File configFile) {
		try {
			config.load(configFile);
		} catch (IOException e) {
			e.printStackTrace(); //TODO: Logger this
		} catch (InvalidConfigurationException e) {
			e.printStackTrace(); //TODO: Logger this also
		}
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return config.getBoolean(path, def);
	}
}
