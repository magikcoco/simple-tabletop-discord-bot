package com.magikcoco.manager.thread;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;

public class ChargenThreadManager extends ThreadManager {

    public ChargenThreadManager(Member player, @NotNull ThreadChannel thread){
        this.thread = thread;
        this.gameCode = thread.getName().split(" ")[1];
        this.players = getPlayersArray(); //this sets the game code
        this.players = new Member[]{player}; //this is the single-person array we want
        if (game == null) {
            //send a message in the thread
            thread.sendMessage("/help for chargen commands\nSomething went wrong, please set the game").queue();
            //log information
            lm.logError("Failed to set game for chargen thread manager in "+thread.getName());
        } else {
            //send a message in the thread
            thread.sendMessage("/help for chargen commands").queue();
            //log information
            lm.logInfo("New Chargen ThreadManager made in thread "+thread.getName());
        }
        thread.addThreadMember(player).queue();
    }

    //TODO: complete chargen functionality
}
