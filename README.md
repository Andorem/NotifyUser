# NotifyUser
### *A simple Bukkit chat notification plugin*
* **1.13-1.15.2:** Compatible with NotifyUser versions 1.2.x.
* **1.9-1.12.x:** Compatible with NotifyUser versions 1.1.x.
* **1.7.10-1.8.x:** Compatible with NotifyUser versions 1.0.x.

##### Note on 1.13+
Due to client changes in Minecraft clients for v1.13 and above, the tab complete functionality is unfortunately no longer supported.
This feature is still available, of course, with the built in tab completion for the `/nu [name]` command.

## About

NotifyUser is a quick-and-easy chat plugin for sending and receiving chat notifications. When a player "tags" another user in the chat area (e.g. "@Andorem"), the username will become highlighted and the pinged user will receive a customizable alert, from sounds already available in Minecraft.

 As a result of a desire for a bit more customizable ping plugin, NotifyUser has several configuration preferences for choosing different sounds, muting notifications, tagging & pinging options.

## Installing

Though customizable, NotifyUser works out of the box and requires no extra set-up to use:

1. **Drop** the Jar file into your plugin folder.
2. **Restart** your server.
3. **Tweak** the default configuration file. (Optional)
### Plugin Compatibility

Please refer to the following compatibilities and known conflicts with other chat plugins when installing NotifyUser. If you encounter a new issue or wish for compatability with another plugin, [post it here](https://www.spigotmc.org/threads/notifyuser.266182/#post-2617787).

#### Known Compatibilities
* EssentialsChat
* FactionsUUID - *v1.6.9.5-U0.5.10+*
  * Set `hooks.factions` to **true**

#### Known Conflicts
* TownyChat
* mcMMO
## How to use

### Pinging a player

There are two ways to send notifications to other users:

**Public chat:** @`tagging`
To ping a player from the chat, simply "tag" them with the appropriate symbol: `Hey, @TeddyRoosevelt! What's up?`. The tagged username will become highlighted and the mentioned user will receive a sound alert. This is not case-sensitive, and it recognizes partial usernames (configurable), so `@Teddy` and `@teddyroosevelt` work just as well.

**Via Command:** `/nu [username]`
If you want to ping a player without making a public mention, you can type `/nu [username]` without any symbol in order to notify them directly. They will receive a message saying who pinged them and a sound alert (these can be changed). The same name sensitivity as the first method applies.

### Muting notifications
If a player wishes **mute receive sound alerts or on-command pings**, they can use `/nu mute`. By default, text tag-highlighting will still go through, but both sound alerts and pings made through the `/nu ` command will be blocked. This acts as a toggle, and can be turned off by typing `/nu mute` again.

## Commands

All commands can also be executed with **/notifyuser** and **/nfy**.

### General commands:

* **/nu [username]** - Send a notification to a specific user without typing into public chat.
* **/nu help** - Show all available NotifyUser commands.
* **/nu mute** - Toggle mute/unmute for incoming notifications.

### Admin commands:

* **/nu set [SOUND_NAME]** - Set the notification sound to be heard by all players. (Refer to these lists of sounds: [pre-1.9](http://jd.bukkit.org/org/bukkit/Sound.html) | [1.9+](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html))
* **/nu reload** - Reloads the NotifyUser configuration file.

## Permissions
* **notifyuser.*** - Gives access to all NotifyUser commands. (default: op)
* **notifyuser.player.*** - Gives access to all standard player permissions.
  * **notifyuser.player.send** - Allows a player to send in-chat and sound notifications. (default: true)
  * **notifyuser.player.receive** - Allows a player to receive in-chat and sound notifications. (default: true)
  *  **notifyuser.player.mute** - Allows a player to mute any incoming notification sounds. (default: true)
  *  **notifyuser.player.highlight** - Allows a player to see their name highlighted in chat when pinged. (default: true)

* **notifyuser.admin.*** - Gives access to all admin permissions.
   * **notifyuser.admin.set** - Allows you to set the notification sound for all players. (default: op)
   * **notifyuser.admin.reload** - Allows you to use the reload command to update the config. (default: op)

* **notifyuser.override.*** - Gives access to all overriding permissions.

  *  **notifyuser.override.notify** - Always receive silent notification alerts regardless of notify option in config. (default: false)
  *  **notifyuser.override.highlightall** - Always highlight all in-chat notifications regardless of highlight-for-all option in config (default: false)

* **notifyuser.anonymous.*** - Gives access to all anonymous permissions.
  *  **notifyuser.anonymous.send** - Always send silent notifications without revealing your username. (default: false)
  *  **notifyuser.anonymous.receive** - Always see the username of anyone who sends you a silent notification (takes precedence over NotifyUser.anonymous.sender) (default false)

##Setting up
There are several optional preferences for customization within the `config.yml` if you so desire. After editing and saving the file, either restart the server or type `/nu reload` to update the plugin's settings.

```yaml 
notifications:
 sound-effect: BLOCK_NOTE_BLOCK_PLING
  # Name (all caps) of the sound to play when pinged.
  # (Refer to the list of sounds above.
 volume: 1.0
  # Volume at which the notification will be heard (0.0 - 1.0).
 pitch: 2
  # Pitch at which the notification will be heard (1 = normal).

chat:
 symbol: '@'
   # Symbol(s) used when typing in chat to ping a user (e.g. @ # *)
 highlight-color: '&d'
   # Set the color that tagged names will be highlighted with (e.g. &a, &b, &c...)
 message-color: '&b'
   # Insert the default color of your chat to display after the tagged name.
 highlight-for-all: false
   # True or false. If true, all players will see every highlighted tag. If false, only the player who was tagged will see it highlighted.
 min-name-length: 3
   # One must type at least this many letters to ping another player.
   # Anything below will not be highlighted or make a ping noise.
 allow-partial: true
  # True or false. If true, partial names such as [USER=33428]@Mary[/USER] can ping @MaryAnn32. If false, only @MaryAnn32 can be used to ping.
 notify: true
  # True, false, or anonymous. If true, the player pinged by /nu [username] will be told who sent it. If false, no message will be sent. If anonymous, the message will not specify the sender.

mute:
 sound: true
   # True or false. If true, /nu mute will disable sound notifications for this player.
 highlight: true
   # True, false, or all. If true, /nu mute will disable in-chat notifications targeted to player. If all, it will disable all in-chat notifications.

messages: # (Use '&' color codes)
 errors:
  no-perm: '&cYou do not have permission to perform that command.'
   # Message seen after using a command without proper permissions.
  not-min: '&cYou must type at least {min} characters to ping!'
   # Tried to use /nu [username] with too little characters.
  not-found: '&cError: &4Player not found.'
   # Pinged a player that is offline or does not exist.
 mute:
  toggle: 'Incoming notifications have been {toggle}.'
   # After toggling notifications with /nu mute.
  alert: '&c{receiver} has muted notifications.'
   # Pinged a player whose notifications are muted.
 ping:
  from: Notification sent to {receiver}.
   # Successfully sent a notification using /nu [username].
  to: '&aYou have been pinged!'
   # Successfully received a notification using /nu [username].
  anon: '&aYou have been pinged!'
   # Used instead when receiving an anonymous notification.`

hooks: # Enable these hooks for plugin compatability
 factions: false
  # Set to true if using a supported factions plugin (see Compatability section)
```    
### The notification alert

#### Changing the sound

You can change **the sound a player hears when they are pinged** with the `/nu set [SOUND_NAME]` command in-game, or by editing `notifications.sound-effect` in the config file.

To determine the name of the alert you want, refer to the list of sounds: [pre-1.9](http://jd.bukkit.org/org/bukkit/Sound.html) | [1.9+](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html. Be sure to include any underscores and correct capitalization.

#### Controlling volume and pitch

If you want to set **how loud or high-pitched the sound is**, you can do so by editing `notifications.volume` and `notifications.pitch` in the config file, respectively.

The `volume` determines how loud or quiet the alert noise will be when mentioned or pinged. 1.0 corresponds to full volume.

The `pitch` determines how high or low pitched it will sound. 1 represents the noise's normal range of pitch.

### Tweak player mentions

#### The tag handle

The tag handle is **the symbol that a player must type right before a username** in order to activate the ping mention. If a valid username is tagged, it will become **highlighted** in the chat area for easy noticing, and a ping will be sent to that player (unless it is muted).

By default, the tag handle is `@`, however it can be easily changed by editing `chat.symbol` in the config. Though a single special character is reccomended, the tag handle can be any one string you wish (e.g. `>>` or `Wololo`).

You can also **change the color of tagged usernames** when highlighted by adding an "&" color code in `chat.highlight-color`.

#### Choosing how pings work

NotifyUser gives you the option of tweaking h**ow a ping mention will be activated** in the main chat or silent commands. You can change the minimum amount of letters of a username that a person must type in order to ping someone with `chat.min-num-length`. Anything tagged below this number will be ignored, not highlighted, and no alert made.

To determine whether or not an on-command ping will **alert the recipient or not,** change `chat.notify` to false. When true, the command `/nu [username]` will send both a sound alert and silent configurable message `"SENDER has pinged you!" to the user. Alternatively, setting this to 'anonymous' will send a similar message without the sender's name.

[[Source Code]](https://github.com/Andorem/NotifyUser)