<h1><a id="user-content-playernotify" class="anchor" href="#playernotify" aria-hidden="true"><span class="octicon octicon-link"></span></a>PlayerNotify</h1>

<h4><a id="user-content-a-simple-bukkit-chat-notification-plugin" class="anchor" href="#a-simple-bukkit-chat-notification-plugin" aria-hidden="true"><span class="octicon octicon-link"></span></a><em>A simple Bukkit chat notification plugin</em></h4>

<h2><a id="user-content-about" class="anchor" href="#about" aria-hidden="true"><span class="octicon octicon-link"></span></a>About</h2>

<p>PlayerNotify is a quick-and-easy chat plugin for sending and receiving chat notifications. When a player "tags"    another username in the chat area (e.g. "@Andorem"), the username will become highlighted and the pinged user will receive a customizable alert, using sounds already available in vanilla Minecraft. </p>

<p>There are many chat ping plugins that work great, but I was looking for something just a bit more customizable. As a result, PlayerNotify has several configuration preferences (<code>/plugins/PlayerNotify/config.yml</code>) such as choosing your desired sound, muting notifications, determining how players can be tagged, and the minimum requirements required to activate a ping. </p>

<p>You can simply grab the <a href="http://dev.bukkit.org/bukkit-plugins/playernotify">jar</a> and use it right away or, if you so wish, tweak the default configuration file. It's up to you!</p>

<h2><a id="user-content-installing" class="anchor" href="#installing" aria-hidden="true"><span class="octicon octicon-link"></span></a>Installing</h2>

<p>Though customizable, PlayerNotify works out of the box and requires no extra set-up to use. Just drop the Jar file into your server's plugin folder, restart, and you're good to go! </p>

<h2><a id="user-content-how-to-use" class="anchor" href="#how-to-use" aria-hidden="true"><span class="octicon octicon-link"></span></a>How to use</h2>

<h3><a id="user-content-pinging-a-player" class="anchor" href="#pinging-a-player" aria-hidden="true"><span class="octicon octicon-link"></span></a>Pinging a player</h3>

<p>There are two ways to send notifications to other users, through public chat tagging and a command. </p>

<p>To ping a player from the chat, simply "tag" them with the appropriate symbol: <code>Hey, @TeddyRoosevelt! What's up?</code>. The tagged username will become highlighted and the mentioned user will receive a sound alert. This is not case-sensitive, and it recognizes partial usernames, so <code>@Teddy</code> and <code>@teddyroosevelt</code> work just as well. </p>

<p>If you want to ping a player without making a public mention, you can type <code>/pn [username]</code> without any symbol in order to notify them directly. They will receive (unless disabled) a message saying who pinged them and a sound alert. The same name sensitivity as the first method applies.</p>

<h3><a id="user-content-muting-notifications" class="anchor" href="#muting-notifications" aria-hidden="true"><span class="octicon octicon-link"></span></a>Muting notifications</h3>

<p>If a player wishes to not receive sound alerts or on-command pings, they can use <code>/pn mute</code>. Text tag-highlighting will still go through, but both sound alerts and pings made through the <code>/pn</code> command will be blocked. This acts as a toggle, and can be turned off by typing <code>/pn mute</code> again.</p>

<h2><a id="user-content-commands" class="anchor" href="#commands" aria-hidden="true"><span class="octicon octicon-link"></span></a>Commands</h2>

<p>All commands can also be executed with <strong>/playernotify</strong> and <strong>/pf</strong>.</p>

<h3><a id="user-content-standard-commands" class="anchor" href="#standard-commands" aria-hidden="true"><span class="octicon octicon-link"></span></a>Standard commands:</h3>

<p><strong>/pln [username]</strong> - Send a notification to a specific user without typing into public chat.</p>

<p><strong>/pln help</strong> - Show all available PlayerNotify commands.</p>

<p><strong>/pln mute</strong> - Toggle mute/unmute for incoming notifications.</p>

<h3><a id="user-content-admin-commands" class="anchor" href="#admin-commands" aria-hidden="true"><span class="octicon octicon-link"></span></a>Admin commands:</h3>

<p><strong>/pln set [SOUND_NAME]</strong> - Set the notification sound to be heard by all players. (Refer to <a href="http://jd.bukkit.org/org/bukkit/Sound.html">http://jd.bukkit.org/org/bukkit/Sound.html</a>) </p>

<p><strong>/pln reload</strong> - Reloads the PlayerNotify configuration file.</p>

<h2><a id="user-content-setting-up" class="anchor" href="#setting-up" aria-hidden="true"><span class="octicon octicon-link"></span></a>Setting up</h2>

<p>There are several optional preferences for customization within the <code>config.yml</code> if you so desire. After editing and saving the file, either restart the server or type <code>/pn reload</code> to update the plugin's settings.</p>

<h3><a id="user-content-the-notification-alert" class="anchor" href="#the-notification-alert" aria-hidden="true"><span class="octicon octicon-link"></span></a>The notification alert</h3>

<h4><a id="user-content-changing-the-sound" class="anchor" href="#changing-the-sound" aria-hidden="true"><span class="octicon octicon-link"></span></a>Changing the sound</h4>

<p>You can change the sound a player hears when they are pinged with the <code>/pn set [SOUND_NAME]</code> command in-game, or by editing <code>notifications.sound-effect</code> in the config file.</p>

<p>To determine the name of the alert you want, refer to the list of sounds at jd.bukkit.org/org/bukkit/Sound.html (in the "Enum Constant Summary"). Be sure to include any underscores and correct capitalization. </p>

<h4><a id="user-content-controlling-volume-and-pitch" class="anchor" href="#controlling-volume-and-pitch" aria-hidden="true"><span class="octicon octicon-link"></span></a>Controlling volume and pitch</h4>

<p>If you want to set how loud or high-pitched the sound is, you can do so by editing <code>notifications.volume</code> and <code>notifications.pitch</code> in the config file, respectively.</p>

<p>The <code>volume</code> determines how loud or quiet the alert noise will be when mentioned or pinged. 100 corresponds to full volume. </p>

<p>The <code>pitch</code> determines how high or low pitched it will sound. 1 represents the noise's normal range of pitch. Feel free to experiment, but it might be best left alone. </p>

<h3><a id="user-content-tweak-player-mentions" class="anchor" href="#tweak-player-mentions" aria-hidden="true"><span class="octicon octicon-link"></span></a>Tweak player mentions</h3>

<h4><a id="user-content-the-tag-handle" class="anchor" href="#the-tag-handle" aria-hidden="true"><span class="octicon octicon-link"></span></a>The tag handle</h4>

<p>The tag handle is the symbol that a player must type right before a username in order to activate the ping mention. If a valid username is tagged, it will become highlighted in the chat area for easy noticing, and a ping will be sent to that player (unless it is muted). </p>

<p>By default, the tag handle is <code>@</code>, however it can be easily changed by editing <code>chat.symbol</code> in the config. Though a single special character is reccomended, the tag handle can be any one string you wish (e.g. <code>&gt;&gt;</code> or <code>Wololo</code>).</p>

<h4><a id="user-content-choosing-how-pings-work" class="anchor" href="#choosing-how-pings-work" aria-hidden="true"><span class="octicon octicon-link"></span></a>Choosing how pings work</h4>

<p>PlayerNotify gives you the option of tweaking how a ping mention will be activated in the main chat or silent commands. You can change the minimum amount of letters of a username that a person must type in order to ping someone with <code>chat.min-num-length</code>. Anything tagged below this number will be ignored, not highlighted, and no alert made.</p>

<p>To determine whether or not an on-command ping will alert the recipient or not, change <code>chat.notify</code> to false. When true, the command <code>/pn [username]</code> will send both a sound alert and silent message `"SENDER has pinged you!" to the user. </p>

<h2><a id="user-content-permissions" class="anchor" href="#permissions" aria-hidden="true"><span class="octicon octicon-link"></span></a>Permissions</h2>

<p><strong>PlayerNotify.</strong>* - Gives access to all PlayerNotify commands. (default: op)</p>

<p><strong>PlayerNotify.player.</strong>* - Gives access to all standard player permissions.</p>

<p><strong>PlayerNotify.player.send</strong> - Allows a player to send in-chat and sound notifications. (default: true)</p>

<p><strong>PlayerNotify.player.receive</strong> - Allows a player to receive in-chat and sound notifications. (default: true)</p>

<p><strong>PlayerNotify.player.mute</strong> - Allows a player to mute any incoming notification sounds. (default: true)</p>

<p><strong>PlayerNotify.admin.</strong>* - Gives access to all admin permissions.</p>

<p><strong>PlayerNotify.admin.set</strong> - Allows you to set the notification sound for all players. (default: op)</p>

<p><strong>PlayerNotify.admin.reload</strong> - Allows you to use the reload command to update the config. (default: op)</p>
