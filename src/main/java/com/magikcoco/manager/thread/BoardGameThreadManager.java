package com.magikcoco.manager.thread;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;

public class BoardGameThreadManager extends ThreadManager {

    public BoardGameThreadManager(@NotNull ThreadChannel thread){
        this.thread = thread;
        gameCode = thread.getName().split(" ")[1];
        players = getPlayersArray();
        if(game == null){
            //send a message
            thread.sendMessage("/help for game commands\nsomething went wrong, please set the game").queue();
            //log the error
            lm.logError("Could not set the game for BoardGameManager in "+thread.getName());
        } else {
            //send a message
            thread.sendMessage("/help for game commands").queue();
            //log info
            lm.logInfo("New BoardGameManger in thread "+thread.getName());
        }
    }

    //TODO: complete board game functionality
}
