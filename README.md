# Mumble Link Client
*Ce programme permet la communication entre votre serveur Minecraft et le [MumbleLink Core](https://github.com/EliottBovel/MumbleLinkCore), vos joueurs doivent avoir le mod [MumbleLink](https://legacy.curseforge.com/minecraft/mc-mods/mumblelink) pour Minecraft de [snipingcoward](https://legacy.curseforge.com/members/snipingcoward).*
## Support
En cas de soucis lors de l'installation ou de l'utilisation, vous pouvez m'envoyer un message sur [Discord](https://discord.ezalys.fr), j'essaierai de vous aider dans la mesure du possible !

## Installation
1. Installez le [MumbleLink Core](https://github.com/EliottBovel/MumbleLinkCore).
2. [Téléchargez](https://github.com/EliottBovel/MumbleLinkClient/releases) et placez le plugin dans votre dossiers /plugins .
3.  Allumez puis éteignez votre serveur Minecraft.
4. Modifiez le fichier de configuration dans /plugins/MumbleLinkClient/config.yml

> automatic-move-spec => true si vous voulez que les joueurs en spectateurs passent directement dans le canal Spectateur
> 
> default-mumble-name => Nom du canal Root du Mumble
> 
> api-key => La clef d'API que vous avez choisis dans la configuration du MumbleLink Core
> 
> join-link: => Lien vers le serveur web du MumbleLink Core (changez juste le example.org par l'IP ou le nom de domaine qui redirige vers celle-ci)
>
> socket => IP (en chiffre) et Port du socket du MumbleLink Core

5. Redémarrez votre serveur Minecraft ! Have Fun !

## Commandes
Il y a une unique commande: /mumble ! 
Sans argument, celle-ci permet aux joueurs de se connecter au Mumble.
Avec argument(s), il faut avoir la permission `mumble.admin` ou être OP.

- /mumble start => Redirige tous les joueurs dans le salon Game.
- /mumble stop => Redirige tous les joueurs dans le salon Root.
- /mumble nolink => envoi au joueur la liste de tous les joueurs non link ou non connectés.
- /mumble broadcastnolink => envoi à tous les joueurs la liste de tous les joueurs non link ou non connectés.
- /mumble mute PSEUDO/all => Mute un joueur (ou tous les joueurs)
- /mumble unmute PSEUDO/all => Unmute un joueur (ou tous les joueurs)
- /mumble info PSEUDO => Renvoi les informations d'un joueur.
- /mumble move PSEUDO/all game/spectateur => Move un joueur (ou tous les joueurs) dans le salon game ou le salon spectateur

## Liste des Serveurs Minecraft (ou Discord) utilisant le programme:

 - [Ezalys](http://discord.ezalys.fr) : UHC Minecraft

N'hésitez pas à me contacter sur Discord pour ajouter votre serveur ici ! Discord: EliottBvl

## TODO 
*En cours de rédaction.*

## Modification du Programme
Chacun est libre de modifier le programme à sa guise.

Je n'apporterai pas de support aux programmes différents de celui sur ce git.
Si vous voulez participer à l'amélioration du programme, n'hésitez pas à proposer vos changements sur GitHub !

Merci à vous et profitez bien de ce programme !

    


