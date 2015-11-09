# PlayerNotify

####_A simple Bukkit chat notification plugin_

## About

PlayerNotify is a quick-and-easy chat plugin for sending and receiving chat notifications. When a player "tags" another username in the chat area (e.g. "@Andorem"), the username will become highlighted and the pinged user will receive a customizable alert, using sounds already available in vanilla Minecraft.

There are many chat ping plugins that work great, but I was looking for something just a bit more customizable. As a result, PlayerNotify has several configuration preferences (`/plugins/PlayerNotify/config.yml`) such as choosing your desired sound, muting notifications, determining how players can be tagged, and the minimum requirements required to activate a ping.

You can simply grab the [jar](http://dev.bukkit.org/bukkit-plugins/playernotify) and use it right away or, if you so wish, tweak the default configuration file. It's up to you!

## Installing

Though customizable, PlayerNotify works out of the box and requires no extra set-up to use. Just drop the Jar file into your server's plugin folder, restart, and you're good to go!

## How to use

### Pinging a player

There are two ways to send notifications to other users, through public chat tagging and a command.

To ping a player from the chat, simply "tag" them with the appropriate symbol: `Hey, @TeddyRoosevelt! What's up?`. The tagged username will become highlighted and the mentioned user will receive a sound alert. This is not case-sensitive, and it recognizes partial usernames, so `@Teddy` and `@teddyroosevelt` work just as well.

If you want to ping a player without making a public mention, you can type `/pn [username]` without any symbol in order to notify them directly. They will receive (unless disabled) a message saying who pinged them and a sound alert. The same name sensitivity as the first method applies.

### Muting notifications

If a player wishes to not receive sound alerts or on-command pings, they can use `/pn mute`. Text tag-highlighting will still go through, but both sound alerts and pings made through the `/pn` command will be blocked. This acts as a toggle, and can be turned off by typing `/pn mute` again.

## Commands

All commands can also be executed with **/playernotify** and **/pf**.

### Standard commands:

**/pln [username]** - Send a notification to a specific user without typing into public chat.

**/pln help** - Show all available PlayerNotify commands.

**/pln mute** - Toggle mute/unmute for incoming notifications.

###Admin commands:

**/pln set [SOUND_NAME]** - Set the notification sound to be heard by all players. (Refer to http://jd.bukkit.org/org/bukkit/Sound.html) 

**/pln reload** - Reloads the PlayerNotify configuration file.

##Setting up
There are several optional preferences for customization within the `config.yml` if you so desire. After editing and saving the file, either restart the server or type `/pn reload` to update the plugin's settings.

###The notification alert

####Changing the sound
You can change the sound a player hears when they are pinged with the `/pn set [SOUND_NAME]` command in-game, or by editing `notifications.sound-effect` in the config file.

To determine the name of the alert you want, refer to the list of sounds at jd.bukkit.org/org/bukkit/Sound.html (in the "Enum Constant Summary"). Be sure to include any underscores and correct capitalization. 

####Controlling volume and pitch
If you want to set how loud or high-pitched the sound is, you can do so by editing `notifications.volume` and `notifications.pitch` in the config file, respectively.

The `volume` determines how loud or quiet the alert noise will be when mentioned or pinged. 100 corresponds to full volume. 

The `pitch` determines how high or low pitched it will sound. 1 represents the noise's normal range of pitch. Feel free to experiment, but it might be best left alone. 

###Tweak player mentions

####The tag handle
The tag handle is the symbol that a player must type right before a username in order to activate the ping mention. If a valid username is tagged, it will become highlighted in the chat area for easy noticing, and a ping will be sent to that player (unless it is muted). 

By default, the tag handle is `@`, however it can be easily changed by editing `chat.symbol` in the config. Though a single special character is reccomended, the tag handle can be any one string you wish (e.g. `>>` or `Wololo`).

####Choosing how pings work
PlayerNotify gives you the option of tweaking how a ping mention will be activated in the main chat or silent commands. You can change the minimum amount of letters of a username that a person must type in order to ping someone with `chat.min-num-length`. Anything tagged below this number will be ignored, not highlighted, and no alert made.

To determine whether or not an on-command ping will alert the recipient or not, change `chat.notify` to false. When true, the command `/pn [username]` will send both a sound alert and silent message `"SENDER has pinged you!" to the user. 

##Permissions
**PlayerNotify.*** - Gives access to all PlayerNotify commands. (default: op)

=======
### Admin commands:

**/pln set [SOUND_NAME]** - Set the notification sound to be heard by all players. (Refer to [http://jd.bukkit.org/org/bukkit/Sound.html](http://jd.bukkit.org/org/bukkit/Sound.html))

**/pln reload** - Reloads the PlayerNotify configuration file.

## Setting up

There are several optional preferences for customization within the `config.yml` if you so desire. After editing and saving the file, either restart the server or type `/pn reload` to update the plugin's settings.

### The notification alert

#### Changing the sound

You can change the sound a player hears when they are pinged with the `/pn set [SOUND_NAME]` command in-game, or by editing `notifications.sound-effect` in the config file.

To determine the name of the alert you want, refer to the list of sounds at jd.bukkit.org/org/bukkit/Sound.html (in the "Enum Constant Summary"). Be sure to include any underscores and correct capitalization.

#### Controlling volume and pitch

If you want to set how loud or high-pitched the sound is, you can do so by editing `notifications.volume` and `notifications.pitch` in the config file, respectively.

The `volume` determines how loud or quiet the alert noise will be when mentioned or pinged. 100 corresponds to full volume.

The `pitch` determines how high or low pitched it will sound. 1 represents the noise's normal range of pitch. Feel free to experiment, but it might be best left alone.

### Tweak player mentions

#### The tag handle

The tag handle is the symbol that a player must type right before a username in order to activate the ping mention. If a valid username is tagged, it will become highlighted in the chat area for easy noticing, and a ping will be sent to that player (unless it is muted).

By default, the tag handle is `@`, however it can be easily changed by editing `chat.symbol` in the config. Though a single special character is reccomended, the tag handle can be any one string you wish (e.g. `>>` or `Wololo`).

#### Choosing how pings work

PlayerNotify gives you the option of tweaking how a ping mention will be activated in the main chat or silent commands. You can change the minimum amount of letters of a username that a person must type in order to ping someone with `chat.min-num-length`. Anything tagged below this number will be ignored, not highlighted, and no alert made.

To determine whether or not an on-command ping will alert the recipient or not, change `chat.notify` to false. When true, the command `/pn [username]` will send both a sound alert and silent message `"SENDER has pinged you!" to the user.

## Permissions

**PlayerNotify.*** - Gives access to all PlayerNotify commands. (default: op)

**PlayerNotify.player.*** - Gives access to all standard player permissions.

**PlayerNotify.player.send** - Allows a player to send in-chat and sound notifications. (default: true)

**PlayerNotify.player.receive** - Allows a player to receive in-chat and sound notifications. (default: true)

**PlayerNotify.player.mute** - Allows a player to mute any incoming notification sounds. (default: true)

**PlayerNotify.admin.*** - Gives access to all admin permissions.

**PlayerNotify.admin.set** - Allows you to set the notification sound for all players. (default: op)

**PlayerNotify.admin.reload** - Allows you to use the reload command to update the config. (default: op)
