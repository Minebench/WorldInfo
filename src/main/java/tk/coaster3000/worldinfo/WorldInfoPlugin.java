package tk.coaster3000.worldinfo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldInfoPlugin extends JavaPlugin implements PluginMessageListener, Listener {

	public static final byte[] ZERO_BYTES = new byte[0];

	private String encoding;
	private String channel;
	private boolean informPlayer;
	private ID_MODE idMode;
	private Logger log;
	private int verbose;

	private boolean registered = false;

	@Override
	public void onEnable() {
		log = getLogger();
		reloadConfigSettings();
		getCommand("reloadWorldInfo").setExecutor(this);
	}

	@Override
	public void onDisable() {
		unregister();
	}

	public void reloadConfigSettings() {
		unregister();
		FileConfiguration config = getConfig();

		File configFile = new File(getDataFolder(), "config.yml");

		if (!configFile.exists()) {
			try {
				config.save(configFile);
			} catch (IOException e) {
				log.log(Level.WARNING, "reload save failed", e);
			}
		} else {
			try {
				config.load(configFile);
			} catch (IOException | InvalidConfigurationException e) {
				log.log(Level.WARNING, "reload save failed", e);
			}
		}
		config.options().copyDefaults(true);

		informPlayer = config.getBoolean("inform-player", false);
		channel = "worldinfo:" + config.getString("plugin-channel", "world_id");
		encoding = config.getString("encoding", "UTF-8");
		idMode = ID_MODE.valueOf(config.getString("mode", ID_MODE.NAME.name()).toUpperCase(Locale.ENGLISH));
		verbose = config.getInt("verbose", 0);

		register();
	}

	public void register() {
		registered = true;
		Bukkit.getPluginManager().registerEvents(this, this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, channel);
		getServer().getMessenger().registerIncomingPluginChannel(this, channel, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("wuuid.reload")) {
			sender.sendMessage(ChatColor.RED + "You do not have permission!");
			return true;
		}

		reloadConfigSettings();
		sender.sendMessage(ChatColor.GOLD + "Reloaded settings.");
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldLoad(WorldLoadEvent event) {
		checkWorldFolder(event.getWorld());
	}

	private void checkWorldFolder(World world) {
		File idFile = new File(world.getWorldFolder(), "world.id");
		if (!idFile.exists()) genID(idFile);
	}

	private void genID(File idFile) {
		try {
			boolean newFile = idFile.createNewFile();
			if (newFile) {
				try(FileWriter fw = new FileWriter(idFile)){
					fw.write(UUID.randomUUID().toString());
				}
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
	}

	public void unregister() {
		if (registered) {
			getServer().getMessenger().unregisterIncomingPluginChannel(this, channel, this);
			getServer().getMessenger().unregisterOutgoingPluginChannel(this, channel);
			HandlerList.unregisterAll((Listener) this);
		}
		registered = false;
	}

	private void sendData(Player player, byte packetID, byte[] data) {
		ByteBuffer buffer = ByteBuffer.allocate(2 + data.length).put(packetID).put((byte) data.length).put
				(data);
		player.sendPluginMessage(this, channel, buffer.array());
	}

	private String getFileID(World w) {
		String ret = null;

		File file = new File(w.getWorldFolder(), "world.id");
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			ret = reader.readLine();
		} catch (IOException e) {
			log.log(Level.WARNING, "", e);
		}
		return ret;
	}

	public void onPluginMessageReceived(String channel, Player player,
	                                    byte[] bytes) {
		log.info("Message received");

		if (!channel.equals(this.channel)) {
			player.sendMessage("Message Recieved but was not the same channel.");
			return;
		}
		try {
			if(verbose >= 3){
				player.sendMessage("Message recieved and sending data...");
			}

			World w = player.getWorld();

			byte[] data = ZERO_BYTES;
			switch (idMode) {
				case NAME:
					data = w.getName().getBytes(encoding);
					break;
				case UUID:
					data = w.getUID().toString().getBytes(encoding);
					break;
				case FILE:
					data = getFileID(w).getBytes(encoding);
					break;
			}

			sendData(player, (byte) 0, data);
		} catch (Throwable t) {
			log.log(Level.WARNING, "", t);
			if (informPlayer && player.isOnline()) player.sendMessage(new String[]{
					ChatColor.RED + "An error occurred sending world UUID!",
					ChatColor.GRAY + "Please check server logs for details..."
			});
		}
	}

	enum ID_MODE {
		UUID, NAME, FILE;
	}
}
