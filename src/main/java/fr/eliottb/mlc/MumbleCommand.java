package fr.eliottb.mlc;

import fr.eliottb.mlc.utils.Request;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static fr.eliottb.mlc.MumbleLinkClient.config;

public class MumbleCommand implements TabExecutor {
    private final MumbleLinkClient main;

    public MumbleCommand(MumbleLinkClient main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mumble.admin") && args.length > 0) {
            sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "start":
                    sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont étés déplacés dans le salon de jeu.");
                    Request.moveall(1);
                    break;
                case "stop":
                    sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont étés déplacés dans le salon Root.");
                    Request.moveall(0);
                    break;
                case "nolink":
                    sender.sendMessage(getNoLink());
                    break;
                case "broadcastnolink":
                    Bukkit.broadcastMessage(getNoLink());
                    break;
                case "mute":
                    if (args.length > 1) {
                        if (Objects.equals(args[1], "all")) {
                            MumbleUser.users.values().forEach(MumbleUser::adminMute);
                            sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont été rendu muets.");
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Joueur inconnu.");
                                break;
                            }
                            MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                            if (user.isConnected()) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Le joueur a été rendu muet.");
                                user.adminMute();
                            } else {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Le joueur n'est pas connecté sur Mumble.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Merci de renseigner le pseudonyme d'un joueur.");
                    }
                    break;
                case "unmute":
                    if (args.length > 1) {
                        if (Objects.equals(args[1], "all")) {
                            MumbleUser.users.values().forEach(MumbleUser::adminUnmute);
                            sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont retrouvés la parole.");
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Joueur inconnu.");
                                break;
                            }
                            MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                            if (user.isConnected()) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Le joueur a retrouvé la parole.");
                                user.adminUnmute();
                            } else {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Le joueur n'est pas connecté sur Mumble.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Merci de renseigner le pseudonyme d'un joueur.");
                    }

                    break;
                case "info":
                    if (args.length > 1) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player == null) {
                            sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Joueur inconnu.");
                            break;
                        }
                        MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                        if (user != null) {
                            StringBuilder sb = new StringBuilder(ChatColor.AQUA + "\nMumble > " + ChatColor.GOLD + player.getDisplayName() + ChatColor.GRAY + ":\n");
                            sb.append(ChatColor.GRAY).append("  Connecté: ");
                            if(user.isConnected()){
                                sb.append(ChatColor.GREEN).append("✔\n");
                            }else{
                                sb.append(ChatColor.DARK_RED).append("✘\n");
                            }
                            sb.append(ChatColor.GRAY).append("  Lié: ");
                            if(user.isLink()){
                                sb.append(ChatColor.GREEN).append("✔\n");
                            }else{
                                sb.append(ChatColor.DARK_RED).append("✘\n");
                            }
                            sb.append(ChatColor.GRAY).append("  Muet serveur: ");
                            if(user.isMute()){
                                sb.append(ChatColor.GREEN).append("✔\n");
                            }else{
                                sb.append(ChatColor.DARK_RED).append("✘\n");
                            }
                            sb.append(ChatColor.GRAY).append("  Muet client: ");
                            if(user.isSelfMute()){
                                sb.append(ChatColor.GREEN).append("✔\n");
                            }else{
                                sb.append(ChatColor.DARK_RED).append("✘\n");
                            }

                            sb.append(ChatColor.GRAY).append("  Sourt: ");
                            if(user.isSelfDeaf()){
                                sb.append(ChatColor.GREEN).append("✔\n");
                            }else{
                                sb.append(ChatColor.DARK_RED).append("✘\n");
                            }
                            sb.append(ChatColor.GRAY).append("  Channel: ");
                            if(!user.isConnected()){
                                sb.append(ChatColor.RED).append("Aucun\n \n");
                            }else if(user.getChannel() == 0){
                                sb.append(ChatColor.BLUE).append("Root\n \n");
                            }else if(user.getChannel() == 1){
                                sb.append(ChatColor.BLUE).append("Game\n \n");
                            }else{
                                sb.append(ChatColor.DARK_GRAY).append("Spectateur\n \n");
                            }
                            sender.sendMessage(sb.toString());
                        }else{
                            sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Joueur non enregistré.");
                        }

                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Merci de renseigner le pseudonyme d'un joueur.");
                    }

                    break;
                case "move":
                    if (args.length > 2) {
                        if (Objects.equals(args[1], "all")) {
                            if (Objects.equals(args[2], "game")) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont étés déplacés dans le salon de jeu.");
                                Request.moveall(1);
                            } else if (Objects.equals(args[2], "spectateur")) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Tous les joueurs ont étés déplacés dans le salon des spectateurs.");
                                Request.moveall(2);
                            } else {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Salon inconnu (game/spectateur).");
                            }
                        }else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player == null) {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Joueur inconnu.");
                                break;
                            }
                            MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                            if (user.isConnected()) {
                                if (Objects.equals(args[2], "game")) {
                                    sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Le joueur a été déplacé dans le salon de jeu.");
                                    user.move(1);
                                } else if (Objects.equals(args[2], "spectateur")) {
                                    sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GREEN + "Le joueur a été déplacé dans le salon des spectateurs.");
                                    user.move(2);
                                } else {
                                    sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Salon inconnu (game/spectateur).");
                                }
                            } else {
                                sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Le joueur n'est pas connecté sur Mumble.");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Merci de renseigner le pseudonyme d'un joueur et un salon (game/spectateur).");
                    }
                    break;
            }
        }else{
            if(sender instanceof Player){
                if(Request.hasMumble()) {
                    Player player = (Player) sender;
                    MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                    if (MumbleUser.isUser(player.getUniqueId())) {

                        TextComponent text1 = new TextComponent(ChatColor.GRAY + "\n \n=========== " + ChatColor.AQUA + "Mumble" + ChatColor.GRAY + " ===========\n");
                        TextComponent text2 = new TextComponent(ChatColor.GRAY + "\n=============================\n \n");
                        TextComponent text = new TextComponent("=> Rejoindre le Mumble Link.");
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Cliquez ici.")).create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, user.getURL()));
                        player.spigot().sendMessage(text1, text, text2);
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.GRAY + "Attendez 5 à 10 secondes et recommencez.\nEnregistrement...");
                    }
                }else{
                    if(sender.hasPermission("mumble.create")) {
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Mumble non créé. Tentative de création..");
                        Request.createServer(config.getString("default-mumble-name"));
                    }else{
                        sender.sendMessage(ChatColor.AQUA + "Mumble > " + ChatColor.RED + "Mumble non créé.");

                    }
                }
            }else{
                sender.sendMessage(ChatColor.GRAY + "Mumble Link par Eliott BOVEL.");
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        String[] tabe = {"mute", "unmute", "nolink", "broadcastnolink", "move", "info", "start", "stop"};
        List<String> tab = new ArrayList<>(Arrays.asList(tabe));
        if (args.length == 0)
            return tab;
        if (args.length == 1 && !tab.contains(args[0].toLowerCase())) {
            for (int i = 0; i < tab.size(); i++) {
                for (int j = 0; j < tab.get(i).length() && j < args[0].length(); j++) {
                    if (tab.get(i).toLowerCase().charAt(j) != args[0].toLowerCase().charAt(j)) {
                        tab.remove(i);
                        i--;
                        break;
                    }
                }
            }
            return tab;
        }
        List<String> list = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        if (args.length == 1) {
            if (Objects.equals(args[0].toLowerCase(), "mute") || Objects.equals(args[0].toLowerCase(), "unmute") || Objects.equals(args[0].toLowerCase(), "move")) {
                list.add("all");
                return list;
            }
        }
        if (args.length == 2 && !list.contains(args[1].toLowerCase())) {
            if (Objects.equals(args[0].toLowerCase(), "move") || Objects.equals(args[0].toLowerCase(), "info")) {
                list.remove("all");
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length() && j < args[1].length(); j++) {
                        if (list.get(i).toLowerCase().charAt(j) != args[1].toLowerCase().charAt(j)) {
                            list.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                return list;
            }
            if (Objects.equals(args[0].toLowerCase(), "mute") || Objects.equals(args[0].toLowerCase(), "unmute")) {
                list.add("all");
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.get(i).length() && j < args[1].length(); j++) {
                        if (list.get(i).toLowerCase().charAt(j) != args[1].toLowerCase().charAt(j)) {
                            list.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                return list;
            }
        }

        String[] tabe2 = {"spectateur", "game"};
        List<String> tab2 = new ArrayList<>(Arrays.asList(tabe2));

        if (args.length == 2) {
            if (Objects.equals(args[0].toLowerCase(), "move")) {
                return tab2;
            }
        }

        if (args.length == 3 && !tab2.contains(args[2].toLowerCase())) {
            for (int i = 0; i < tab2.size(); i++) {
                for (int j = 0; j < tab2.get(i).length() && j < args[2].length(); j++) {
                    if (tab2.get(i).toLowerCase().charAt(j) != args[2].toLowerCase().charAt(j)) {
                        tab2.remove(i);
                        i--;
                        break;
                    }
                }
            }
            return tab2;
        }
        return null;
    }

    private String getNoLink() {

        StringBuilder sb = new StringBuilder(ChatColor.DARK_GRAY + "=========================\n");
        Bukkit.getOnlinePlayers().forEach(player -> {
            sb.append(ChatColor.WHITE).append(player.getName()).append(": ");
            if (!MumbleUser.isUser(player.getUniqueId())) {
                sb.append(ChatColor.DARK_RED).append("Non Enregistré");
            }else {
                MumbleUser user = MumbleUser.getUser(player.getUniqueId());
                if (user.isConnected()) {
                    if (!user.isLink()) {
                        sb.append(ChatColor.DARK_RED).append("✘");
                    }
                } else {
                    sb.append(ChatColor.GRAY).append(ChatColor.ITALIC).append("Non Connecté");
                }
                sb.append("\n");
            }
        });
        sb.append(ChatColor.DARK_GRAY).append("=========================");
        return sb.toString();
    }
}

