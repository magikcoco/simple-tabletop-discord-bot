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

    private static Bot INSTANCE = new Bot();
    private String token;
    private JDA discordBot;

    private Bot(){
        try {
            URL resource = getClass().getResource("/bot-token.config");
            token = new String(Files.readAllBytes(Paths.get(resource.toURI())));
            discordBot = JDABuilder.createDefault(token) //create the bot
                    .setActivity(Activity.playing("Tabletop Games")) //set the activity displayed under bot in server
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
        discordBot.updateCommands().addCommands(
                //ping slash command, for testing purposes
                Commands.slash("ping", "Pong!")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)),
                //charcreate slash command, for creating a character for the specified game
                Commands.slash("charcreate","Create a character for the specified game")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND))
                        .addOption(OptionType.STRING, "game", "The game to create a character for")
        ).queue();
    }

    private void addListeners(){
        discordBot.addEventListener(new SlashCommandEventListener());
    }

    /**
     * @return the instance of Bot
     */
    public static Bot getInstance(){
        //singleton instance
        return INSTANCE;
    }
}
