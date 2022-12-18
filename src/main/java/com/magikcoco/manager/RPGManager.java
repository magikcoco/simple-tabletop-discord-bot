package com.magikcoco.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.List;

public class RPGManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private List<Member> players;
    private Member gameMaster;
    private ThreadChannel gameThread;
    private String game;
    private boolean autoGM;

    public RPGManager(List<Member> players, ThreadChannel gameThread){
        this.players = players;
        this.gameThread = gameThread;
        autoGM = false;
        gameMaster = null;
        game = this.gameThread.getName().split(" ")[1];
        gameThread.sendMessage("/help for RPG commands").queue();
        lm.logInfo("New RPGManager in thread '"
                +this.gameThread.getName()
                +"' for game code '"+game+"'");
    }

    public List<Member> getPlayers(){
        return players;
    }

    public Member getGameMaster(){
        return gameMaster;
    }

    public ThreadChannel getGameThread(){
        return gameThread;
    }

    public boolean isAutoGM(){
        return autoGM;
    }

    //TODO: complete TTRPG functionality
    //TODO: permanency for RPG managers
}
