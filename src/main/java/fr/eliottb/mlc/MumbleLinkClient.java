package fr.eliottb.mlc;

import fr.eliottb.mlc.utils.Request;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


public final class MumbleLinkClient extends JavaPlugin {
    static public MumbleLinkClient instance;
    static public FileConfiguration config;
    static BukkitTask task;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        config = this.getConfig();

        Request.createServer(config.getString("default-mumble-name"));

        if(Request.disabling) return;
        task = Bukkit.getScheduler().runTaskTimer(this, Request::updateDataUsers, 0L, 60L);

        getServer().getPluginManager().registerEvents(new MumbleListener(), this);

        getCommand("mumble").setExecutor(new MumbleCommand(this));
    }

    @Override
    public void onDisable() {
        if(task != null) task.cancel();
        Request.deleteServer();
    }
}
