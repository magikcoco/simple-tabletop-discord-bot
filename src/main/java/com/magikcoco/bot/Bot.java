package com.magikcoco.bot;

import com.magikcoco.listeners.SlashCommandEventListener;
import com.magikcoco.listeners.TextMessageListener;
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
        //TODO: see #1, create the bot instance from this method instead
        if(BOT == null){
            Path pathToTokenFile = Paths.get(absolutePath);
            BOT = new Bot(new String(Files.readAllBytes(pathToTokenFile)));
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
                //chargen slash command, used to initiate character creation process
                Commands.slash("chargen","Create a character for the specified game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The TTRPG to create a character sheet for",true,true),
                //ping slash command, for testing purposes
                Commands.slash("help", "get help with all the commands from this bot")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                Commands.slash("ping", "Pong!")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //startbg slash command, used to initiate a board game
                Commands.slash("startbg","Start a play-by-post board game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The board game to play", true, true),
                //startrpg slash command, used to initiate a ttrpg
                Commands.slash("startrpg","Start a play-by-post ttrpg")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The ttrpg to play", true, true)
        ).queue();
    }

    /**
     * adds listeners to the bot
     */
    public void addListeners(){
        //method for adding listeners to the bot
        jda.addEventListener(new SlashCommandEventListener()); //listener for slash commands
        jda.addEventListener(new TextMessageListener()); //listener for text messages
    }

    /**
     * @return the name of the discord bot
     */
    public String getBotName(){
        return botName;
    }
}
