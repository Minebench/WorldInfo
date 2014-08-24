package tk.coaster3000.worldinfo;

import java.io.File;

public abstract class ConfigProvider {

	public String getString(String path) {
		return getString(path, null);
	}

	public int getInt(String path) {
		return getInt(path, 0);
	}

	public long getLong(String path) {
		return getLong(path, 0);
	}

	public <T extends Enum> T getEnum(String path, Class<T> type) {
		return Enum.valueOf(type, getString(path, ""));
	}

	public <T extends Enum> T getEnum(String path, Class<T> type, T def) {
		T ret = getEnum(path, type);
		if (ret != null) return ret;

		set(path, def.name());
		return def;
	}

	public abstract long getLong(String path, long def);

	public abstract int getInt(String path, int def);

	public abstract String getString(String path, String def);

	public abstract void set(String path, Object value);

	public abstract File getConfigFile();

	public void save() {
		save(getConfigFile());
	}

	public abstract void save(File configFile);

	public void load() {
		load(getConfigFile());
	}

	public abstract void load(File configFile);

	public boolean getBoolean(String path) {
		return getBoolean(path, false);
	}

	public abstract boolean getBoolean(String path, boolean def);
}
