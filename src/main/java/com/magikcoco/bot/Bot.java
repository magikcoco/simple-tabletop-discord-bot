package com.magikcoco.bot;

import com.magikcoco.eventlisteners.SlashCommandEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Bot {

    private static Bot INSTANCE = new Bot(); //singleton instance
    private String token; //holds the bot token
    private JDA discordBot; //JDA instance for connecting to discord

    private Bot(){
        try {
            //load the token from file
            URL resource = getClass().getResource("/bot-token.config");
            token = new String(Files.readAllBytes(Paths.get(resource.toURI())));
            //create the bot with the token
            discordBot = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("Tabletop Games")) //activity displayed under bot in server
                    .build(); //build the JDA
            addListeners();
            addSlashCommands();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(9001);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(9002);
        }
    }

    private void addSlashCommands(){
        //method for adding slash commands to the bot
        discordBot.updateCommands().addCommands(
                //ping slash command, for testing purposes
                Commands.slash("ping", "Pong!")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //charcreate slash command, used to initiate character creation process
                Commands.slash("charcreate","Create a character for the specified game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The TTRPG to create a character sheet for"),
                //startboardgame slash command, used to initiate a board game
                Commands.slash("startboardgame","Start a play-by-post board game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The board game to play"),
                //startttrpg slash command, used to initiate a ttrpg
                Commands.slash("startttrpg","Start a play-by-post ttrpg")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The ttrpg to play")
        ).queue();
    }

    private void addListeners(){
        //method for adding listeners to the bot
        discordBot.addEventListener(new SlashCommandEventListener()); //listener for slash commands
    }

    /**
     * @return the instance of Bot
     */
    public static Bot getInstance(){
        //singleton instance
        return INSTANCE;
    }
}
