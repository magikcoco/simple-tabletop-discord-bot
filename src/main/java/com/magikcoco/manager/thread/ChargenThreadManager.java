package com.magikcoco.manager.thread;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;

public class ChargenThreadManager extends ThreadManager {

    private Member player;

    public ChargenThreadManager(Member player, @NotNull ThreadChannel thread){
        this.thread = thread;
        this.gameCode = thread.getName().split(" ")[1];
        this.players = getPlayersArray(); //this sets the game code
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
        this.player = player;
        thread.addThreadMember(player).queue();
    }

    public ChargenThreadManager(Member player, @NotNull ThreadChannel thread, boolean add){
        this.thread = thread;
        this.gameCode = thread.getName().split(" ")[1];
        this.players = getPlayersArray(); //this sets the game code
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
        this.player = player;
        if(add){
            thread.addThreadMember(player).queue();
        }
    }

    public Member getPlayer(){
        return player;
    }

    //TODO: complete chargen functionality
}
