package fr.eliottb.mlc.utils;

import fr.eliottb.mlc.MumbleLinkClient;
import fr.eliottb.mlc.MumbleUser;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Request {

    static public String server;
    static public boolean disabling = false;
    static private int timeout = 0;
    public static List<UUID> mutedByAdmin = new ArrayList<>();

    static private JSONObject request(String req) {
        if(disabling) return null;
        try {
            Socket socket = new Socket(MumbleLinkClient.config.getString("socket.ip"), MumbleLinkClient.config.getInt("socket.port"));
            socket.setSoTimeout(10*1000);
            if (socket.isClosed()) {
                Bukkit.getLogger().log(Level.WARNING, "Une erreur est survenue durant la connexion au Mumble Link Core.");
                return null;
            }

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF(req);
            String line = in.readUTF();
            out.close();
            in.close();
            socket.close();
            return new JSONObject(line);
        } catch (ConnectException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Impossible de se connecter au Mumble Link Core. Desactivation du plugin.");
            disabling = true;
            Bukkit.getPluginManager().disablePlugin(MumbleLinkClient.instance);
            return null;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Une erreur est survenue durant l'envoi d'une requête Mumble Link.");
            return null;
        }
    }

    static public boolean hasMumble() {
        return server != null && checkServer(server);
    }

    static private boolean checkErrors(JSONObject rep) {
        try {
            if (rep == null) return false;
            if (!rep.has("code")) return false;
            switch (rep.getInt("code")) {
                case 403:
                    Bukkit.getLogger().log(Level.SEVERE, "La clé d'API n'est pas valide. Desactivation du plugin.");
                    disabling = true;
                    Bukkit.getPluginManager().disablePlugin(MumbleLinkClient.instance);
                    return false;
                case 500:
                case 404:
                    Bukkit.getLogger().log(Level.WARNING, "Une erreur est survenue durant l'execution d'une requête Mumble Link: " + rep.getString("error"));
                    return false;
                default:
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean checkServer(String uuid) {

        try {
            JSONObject req = new JSONObject();
            req.put("action", "check-server");
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            req.put("server", uuid);
            JSONObject rep = request(req.toString());
            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public String createServer(String name) {
        try {
            deleteServer();
            JSONObject req = new JSONObject();
            req.put("action", "create");
            req.put("title", name);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            if (checkErrors(rep)) {
                server = rep.getString("id");
                return rep.getString("id");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean deleteServer() {
        try {
            if (!hasMumble()) return true;
            JSONObject req = new JSONObject();
            req.put("action", "delete");
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            req.put("server", server);
            JSONObject rep = request(req.toString());
            if(checkErrors(rep) || !checkServer(server)){
                MumbleUser.users.clear();
                server = null;
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    static public void updateDataUsers() {
        Bukkit.getScheduler().runTaskAsynchronously(MumbleLinkClient.instance, () -> {
            if (!hasMumble()) return;
            if (Bukkit.getOnlinePlayers().size() == 0) {
                timeout++;
                if (timeout == 100) {
                    timeout = 0;
                   // Request.deleteServer(); TODO REWORK DELETED DETECTION
                }
                return;
            }
            timeout = 0;
            JSONArray data = getUsersInfo();
            if (data == null) return;
            data.forEach(user -> {
                JSONObject JSONUser = (JSONObject) user;
                MumbleUser MUser = MumbleUser.usersById.get(JSONUser.getString("id"));
                if (MUser == null) return;
                MUser.setConnected(JSONUser.getBoolean("connected"));
                MUser.setMute(JSONUser.getBoolean("mute"));
                MUser.setSelfMute(JSONUser.getBoolean("selfMute"));
                MUser.setSelfDeaf(JSONUser.getBoolean("selfDeaf"));
                MUser.setLink(JSONUser.getBoolean("link"));
                MUser.setChannel(JSONUser.getInt("channel"));

                if ((!MUser.isMinecraftOnline() || !MUser.isLink() || mutedByAdmin.contains(MUser.uuid)) && !MUser.isMute() && MUser.isConnected())
                    MUser.systemMute();
                if (MUser.isMinecraftOnline() && MUser.isLink() && !mutedByAdmin.contains(MUser.uuid) && MUser.isMute() && MUser.isSystemMuted() && MUser.isConnected())
                    MUser.systemUnmute();
            });
        });
    }


    static public String createUser(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "create-user");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            if (checkErrors(rep)) {
                new MumbleUser(Bukkit.getPlayer(username).getUniqueId(), rep.getString("id"));
                return rep.getString("id");

            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean muteall() {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "mute-all");
            req.put("server", server);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean mute(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "mute");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean unmuteall() {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "unmute-all");
            req.put("server", server);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }


    static public boolean unmute(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "unmute");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean moveall(int channel) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "move-all");
            req.put("server", server);
            req.put("channel", channel);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean move(String username, int channel) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "move");
            req.put("server", server);
            req.put("username", username);
            req.put("channel", channel);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean setAdminPerm(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "set-admin-perms");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public boolean setPlayerPerm(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "set-player-perms");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());

            return checkErrors(rep);
        } catch (Exception e) {
            return false;
        }
    }

    static public JSONObject getUserInfo(String username) {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "get-user-info");
            req.put("server", server);
            req.put("username", username);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());
            if (checkErrors(rep)) return rep.getJSONObject("data");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    static public JSONArray getUsersInfo() {
        try {
            if (!hasMumble()) createServer(MumbleLinkClient.config.getString("default-mumble-name"));
            JSONObject req = new JSONObject();
            req.put("action", "get-users-info");
            req.put("server", server);
            req.put("api-key", MumbleLinkClient.config.getString("api-key"));
            JSONObject rep = request(req.toString());
            if (checkErrors(rep)) return rep.getJSONArray("data");
            return null;
        } catch (Exception e) {
            return null;
        }
    }


}
