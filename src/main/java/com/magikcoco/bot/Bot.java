package com.magikcoco.bot;

import com.magikcoco.listeners.*;
import com.magikcoco.manager.LoggingManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Bot {

    private static Bot BOT;
    private JDA jda; //JDA instance for connecting to discord
    private String botName;
    private static LoggingManager lm = LoggingManager.getInstance();

    private Bot(String token){
        //create the bot with the token
        jda = JDABuilder.createDefault(token) //default with token
                .setActivity(Activity.playing("Tabletop Games")) //activity displayed under bot in server
                .build(); //build the JDA
        botName = jda.getSelfUser().getAsTag();
    }

    /**
     * @param absolutePath an absolute path to a text file that holds the token needed to log in with the bot
     * @return the instance of Bot created
     */
    @NotNull
    public static Bot createBotFromToken(String absolutePath) throws IOException {
        if(BOT == null){
            Path pathToTokenFile = Paths.get(absolutePath);
            BOT = new Bot(new String(Files.readAllBytes(pathToTokenFile)));
            lm.logInfo("Bot created from token in file: "+absolutePath);
        }
        return BOT;
    }

    /**
     * @return the instance of this bot
     */
    public static Bot getInstance(){
        return BOT;
    }

    /**
     * adds slash commands to the bot
     */
    public void addSlashCommands(){
        //method for adding slash commands to the bot
        jda.updateCommands().addCommands(
                //TODO: add commands for various thread functionality
                //chargen slash command, used to initiate character creation process
                Commands.slash("chargen","Create a character for the specified game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The TTRPG to create a character sheet for",true,true),
                //help slash command, for displaying available commands
                Commands.slash("help", "Get help with all the commands from this bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //for joining the game as a game master
                Commands.slash("joinasgm", "Join the game in this thread as the game master")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //for joining the game as a player
                Commands.slash("joinasplayer", "Join the game in this thread as a player")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //ping slash command, for testing purposes
                Commands.slash("ping", "Pong!")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //rename slash command, for renaming threads
                Commands.slash("rename", "Renames the current thread to the given name")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "name", "The new name for this thread", true),
                //startbg slash command, used to initiate a board game
                Commands.slash("startbg","Start a play-by-post board game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The board game to play", true, true),
                //startrpg slash command, used to initiate a ttrpg
                Commands.slash("startrpg","Start a play-by-post ttrpg")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The ttrpg to play", true, true)
        ).queue();
        lm.logInfo("Slash commands have been added to the bot");
    }

    /**
     * adds listeners to the bot
     */
    public void addListeners(){
        //method for adding listeners to the bot
        jda.addEventListener(new SlashCommandEventListener()); //listener for slash commands
        jda.addEventListener(new TextMessageListener()); //listener for text messages
        jda.addEventListener(new ThreadDeletionListener()); //listener for channel deletions
        jda.addEventListener(new ThreadCreationListener()); //listener for channel creations
        lm.logInfo("Listeners have been added to the bot");
    }

    /**
     * @return the name of the discord bot
     */
    public String getBotName(){
        return botName;
    }
}
