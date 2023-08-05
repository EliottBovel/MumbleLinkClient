package fr.eliottb.mlc;

import fr.eliottb.mlc.utils.Request;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MumbleUser {

    static public HashMap<UUID, MumbleUser> users = new HashMap<>();
    static public HashMap<String, MumbleUser> usersById = new HashMap<>();

    public UUID uuid;
    public String id;

    private boolean connected = false;
    private boolean mute = false;
    private boolean selfMute = false;
    private boolean selfDeaf = false;
    private boolean link = false;
    private boolean systemMuted = false;
    private int channel = 0;

    public MumbleUser(UUID uuid, String id){
        if(!Request.hasMumble()) return;
        this.uuid = uuid;
        this.id = id;
        users.put(uuid, this);
        usersById.put(id, this);
        Player player = Bukkit.getPlayer(uuid);
        if(player.hasPermission("mumble.admin")) Request.setAdminPerm(id);
        player.performCommand("mumble");
    }

    public String getURL(){
        return MumbleLinkClient.config.getString("join-link").replace("{server}", Request.server).replace("{user}", this.id);
    }

    public boolean move(int channel){
        setChannel(channel);
        if(!isMinecraftOnline()) return false;
        return Request.move(id, channel);
    }

    public boolean systemMute(){
        systemMuted = true;
        return Request.mute(id);
    }
    public boolean adminMute(){
        if(!isMinecraftOnline()) return false;
        Request.mutedByAdmin.add(uuid);
        return true;
    }

    public boolean adminUnmute(){
        if(!isMinecraftOnline()) return false;
        Request.mutedByAdmin.remove(uuid);
        return true;
    }

    public boolean systemUnmute(){
        systemMuted = false;
        return Request.unmute(id);
    }
    public boolean isMinecraftOnline(){
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline();
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isSelfMute() {
        return selfMute;
    }

    public void setSelfMute(boolean selfMute) {
        this.selfMute = selfMute;
    }

    public boolean isSelfDeaf() {
        return selfDeaf;
    }

    public void setSelfDeaf(boolean selfDeaf) {
        this.selfDeaf = selfDeaf;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
    static public boolean isUser(UUID uuid){
        return users.containsKey(uuid);
    }
    static public boolean isUserElseCreate(OfflinePlayer player){
        if(isUser(player.getUniqueId())) return true;
        Request.createUser(player.getName());
        return false;
    }

    static public MumbleUser getUser(UUID uuid){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        isUserElseCreate(player);
        return users.get(uuid);
    }

    public boolean isSystemMuted() {
        return systemMuted;
    }
}
