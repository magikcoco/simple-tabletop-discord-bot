package com.magikcoco.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import java.util.List;

public class BoardGameManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private List<Member> players;
    private ThreadChannel gameThread;
    private String boardGame;
    private boolean gameOver;

    public BoardGameManager(List<Member> players, ThreadChannel gameThread){
        this.players = players;
        this.gameThread = gameThread;
        boardGame = gameThread.getName().split(" ")[1];
        gameOver = false;
        gameThread.sendMessage("/help for game commands").queue();
        lm.logInfo("New BoardGameManger in thread '"
                +gameThread.getName()
                +"' for game code '" + boardGame + "'");
    }

    public List<Member> getPlayers(){
        return players;
    }

    public ThreadChannel getGameThread(){
        return gameThread;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    //TODO: complete board game functionality
    //TODO: permanency for board game managers
}
