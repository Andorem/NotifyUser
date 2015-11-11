# NotifyUser
###*A simple Bukkit chat notification plugin*
Version 1.0.x compatibable with Bukkit 1.7.10+

## About

NotifyUser is a quick-and-easy chat plugin for sending and receiving chat notifications. When a player "tags" another user in the chat area (e.g. "@Andorem"), the username will become highlighted and the pinged user will receive a customizable alert, from sounds already available in Minecraft.

 As a result of a desire for a a bit more customizable ping plugin, UserNotify has several configuration preferences for choosing different sounds, muting notifications, tagging & pinging options.

## Installing

Though customizable, NotifyUser works out of the box and requires no extra set-up to use. Just drop the Jar file into your server's plugin folder, restart, and you're good to go. Or, if you so wish, tweak the default configuration file. It's up to you!

## How to use

### Pinging a player

There are two ways to send notifications to other users, through public chat tagging and a command.

To ping a player from the chat, simply "tag" them with the appropriate symbol: `Hey, @TeddyRoosevelt! What's up?`. The tagged username will become highlighted and the mentioned user will receive a sound alert. This is not case-sensitive, and it recognizes partial usernames, so `@Teddy` and `@teddyroosevelt` work just as well.

If you want to ping a player without making a public mention, you can type `/nu [username]` without any symbol in order to notify them directly. They will receive a message saying who pinged them and a sound alert (unless disabled). The same name sensitivity as the first method applies.

### Muting notifications

If a player wishes to not receive sound alerts or on-command pings, they can use `/nu mute`. Text tag-highlighting will still go through, but both sound alerts and pings made through the `/nu` command will be blocked. This acts as a toggle, and can be turned off by typing `/nu mute` again.

## Commands

All commands can also be executed with **/notifyuser** and **/nfy**.

### General commands:

* **/nu [username]** - Send a notification to a specific user without typing into public chat.
* **/nu help** - Show all available NotifyUser commands.
* **/nu mute** - Toggle mute/unmute for incoming notifications.

### Admin commands:

* **/nu set [SOUND_NAME]** - Set the notification sound to be heard by all players. (Refer to [http://jd.bukkit.org/org/bukkit/Sound.html](http://jd.bukkit.org/org/bukkit/Sound.html))
* **/nu reload** - Reloads the NotifyUser configuration file.

## Permissions

* **NotifyUser.*** - Gives access to all NotifyUser commands. (default: op)
* **NotifyUser.player.*** - Gives access to all standard player permissions.
* **NotifyUser.player.send** - Allows a player to send in-chat and sound notifications. (default: true)
* **NotifyUser.player.receive** - Allows a player to receive in-chat and sound notifications. (default: true)
* **NotifyUser.player.mute** - Allows a player to mute any incoming notification sounds. (default: true)
* **NotifyUser.admin.*** - Gives access to all admin permissions.
* **NotifyUser.admin.set** - Allows you to set the notification sound for all players. (default: op)
* **NotifyUser.admin.reload** - Allows you to use the reload command to update the config. (default: op)

##Config.yml

* config.yml - Configuration preferences for chat and command ping.
* muted.dat - Record of all players that have muted their notifications.

* notifications:
    * sound-effect: 
      * Name (all caps) of the sound to play when pinged.
      * (Refer to [Bukkit's list of sounds](http://jd.bukkit.org/org/bukkit/Sound.html).)
    * volume
      * Volume at which the notification will be heard (100 = loudest).
    * pitch
      * Pitch at which the notification will be heard (1 = normal).
* chat:
    * symbol
      * Symbol(s) used when typing in chat to ping a user (e.g. @ # *)
    * highlight-color
      * Set the color that tagged names will be highlighted with (e.g. &a, &b, &c...)
    * min-name-length
      * One must type at least this many letters to ping another player.
      * Only applies when pinging in public chat. Anything below will not  be highlighted or make a ping noise.
    * notify
      * True or false. If true, the player pinged by /nu [username] will be told who sent it.

## Setting up

There are several optional preferences for customization within the `config.yml` if you so desire. After editing and saving the file, either restart the server or type `/nu reload` to update the plugin's settings.

### The notification alert

#### Changing the sound

You can change the sound a player hears when they are pinged with the `/nu set [SOUND_NAME]` command in-game, or by editing `notifications.sound-effect` in the config file.

To determine the name of the alert you want, refer to the list of sounds [here](jd.bukkit.org/org/bukkit/Sound.html). Be sure to include any underscores and correct capitalization.

#### Controlling volume and pitch

If you want to set how loud or high-pitched the sound is, you can do so by editing `notifications.volume` and `notifications.pitch` in the config file, respectively.

The `volume` determines how loud or quiet the alert noise will be when mentioned or pinged. 100 corresponds to full volume.

The `pitch` determines how high or low pitched it will sound. 1 represents the noise's normal range of pitch. Feel free to experiment, but it might be best left alone.

### Tweak player mentions

#### The tag handle

The tag handle is the symbol that a player must type right before a username in order to activate the ping mention. If a valid username is tagged, it will become highlighted in the chat area for easy noticing, and a ping will be sent to that player (unless it is muted).

By default, the tag handle is `@`, however it can be easily changed by editing `chat.symbol` in the config. Though a single special character is reccomended, the tag handle can be any one string you wish (e.g. `>>` or `Wololo`).

You can also change the color that the tagged username will be highlighted by adding an "&" color code in `chat.highlight-color`.

#### Choosing how pings work

NotifyUser gives you the option of tweaking how a ping mention will be activated in the main chat or silent commands. You can change the minimum amount of letters of a username that a person must type in order to ping someone with `chat.min-num-length`. Anything tagged below this number will be ignored, not highlighted, and no alert made.

To determine whether or not an on-command ping will alert the recipient or not, change `chat.notify` to false. When true, the command `/nu [username]` will send both a sound alert and silent message `"SENDER has pinged you!" to the user.

\[[Source Code](https://github.com/Andorem/NotifyUser)\]
