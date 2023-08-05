package fr.eliottb.mlc;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MumbleListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event){
        if(MumbleUser.isUser(event.getPlayer().getUniqueId())) event.getPlayer().performCommand("mumble");
        MumbleUser.getUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void spectate(PlayerGameModeChangeEvent event){
        if(!MumbleLinkClient.config.getBoolean("automatic-move-spec")) return;
        MumbleUser user = MumbleUser.getUser(event.getPlayer().getUniqueId());
        if(user == null) return;
        if(event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            user.move(2);
        }else{
            user.move(1);
        }
    }
}
