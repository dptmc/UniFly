package io.dpteam.UniFly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	private List enabledWorlds = new ArrayList();
	private List flyCommands = new ArrayList();
	private String flyNotAllowed = "";
	private Permission bypassPermission = new Permission("unifly.bypass");

	public Main() {
		super();
	}

	public void onEnable() {
		this.loadConfig();
		this.getServer().getPluginManager().addPermission(this.bypassPermission);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getLogger().info("[UniFly] Plugin loaded and enabled");
	}

	public void onDisable() {
		this.getServer().getPluginManager().removePermission(this.bypassPermission);
		this.getServer().getLogger().info("[UniFly] Plugin unloaded and disabled");
	}

	public void loadConfig() {
		this.getConfig().addDefault("Enabled worlds", Arrays.asList("plots"));
		this.getConfig().addDefault("Fly commands", Arrays.asList("fly"));
		this.getConfig().addDefault("Messages.Cannot fly", "&cYou cannot fly in this world!");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.enabledWorlds = this.getConfig().getStringList("Enabled worlds");
		this.flyCommands = this.getConfig().getStringList("Fly commands");
		this.flyNotAllowed = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Messages.Cannot fly"));
	}

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		try {
			if (!event.getPlayer().hasPermission(this.bypassPermission) && this.enabledWorlds.contains(event.getFrom().getName()) && !this.enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
				event.getPlayer().setAllowFlight(false);
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerTypeFly(PlayerCommandPreprocessEvent event) {
		try {
			if (event.isCancelled()) {
				return;
			}

			if (!event.getPlayer().hasPermission(this.bypassPermission) && !event.getPlayer().getAllowFlight() && !this.enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
				String command = event.getMessage().contains(" ") ? event.getMessage().split(" ")[0] : event.getMessage();
				command = command.replaceFirst("/", "");
				if (this.flyCommands.contains(command.toLowerCase())) {
					event.getPlayer().sendMessage(this.flyNotAllowed.replace("<name>", event.getPlayer().getName()));
					event.setCancelled(true);
				}
			}
		} catch (Exception var3) {
			var3.printStackTrace();
		}
	}
}
