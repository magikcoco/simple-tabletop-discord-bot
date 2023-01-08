package com.magikcoco.manager.thread;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;

public class RPGThreadManager extends ThreadManager {

    private boolean autoGM = false;
    private Member gameMaster;

    public RPGThreadManager(@NotNull ThreadChannel thread){
        this.thread = thread;
        this.gameCode = this.thread.getName().split(" ")[1];
        this.players = getPlayersArray();
        if(game == null){
            //send message
            thread.sendMessage("/help for RPG commands\nsomething went wrong, please set the game").queue();
            //log error
            lm.logError("There is no game for RPGThreadManager in "+this.thread.getName());
        } else {
            thread.sendMessage("/help for RPG commands").queue();
            lm.logInfo("New RPGThreadManager in thread "+this.thread.getName());
        }
    }

    public boolean hasAutoGM(){
        return autoGM;
    }

    public boolean addGM(Member newGM){
        if(gameMaster == null){
            for (Member player : players) {
                if (player != null) {
                    if (player.equals(newGM)) {
                        return false;
                    }
                }
            }
            gameMaster = newGM;
            dm.updateGMInThreadDocument(thread.getId(), gameMaster);
            return true;
        }
        return false;
    }

    public Member getGameMaster(){
        return gameMaster;
    }

    public boolean removeGM(@NotNull Member gm){
        if(gm.equals(gameMaster)){
            gameMaster = null;
            dm.updateGMInThreadDocument(thread.getId(), gameMaster);
            return true;
        }
        return false;
    }

    //TODO: complete TTRPG functionality
}
