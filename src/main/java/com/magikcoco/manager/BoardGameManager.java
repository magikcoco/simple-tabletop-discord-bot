package com.magikcoco.manager;

import com.magikcoco.game.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoardGameManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private Member[] players;
    private ThreadChannel gameThread;
    private String gameCode;
    private Game game;

    public BoardGameManager(@NotNull ThreadChannel gameThread){
        this.gameThread = gameThread;
        gameCode = gameThread.getName().split(" ")[1];
        players = getPlayerArray();
        if(game == null){
            gameThread.sendMessage("/help for game commands\nsomething went wrong, please set the game").queue();
            lm.logError("A problem occurred making BoardGameManager in thread '"
                    +gameThread.getName()+"' for game code '"+gameCode+"'");
        } else {
            gameThread.sendMessage("/help for game commands").queue();
            lm.logInfo("New BoardGameManger in thread '"
                    +gameThread.getName() +"' for game code '" + gameCode + "'");
        }
    }

    public Member[] getPlayers(){
        return players;
    }

    public boolean addPlayer(Member newPlayer){
        int addIndex = -1;
        for(int i = 0; i < players.length; i++){
            if(players[i] == null){
                addIndex = i;
                break;
            }
        }
        if(addIndex != -1){
            players[addIndex] = newPlayer;
            return true;
        } else {
            return false;
        }
    }

    public ThreadChannel getGameThread(){
        return gameThread;
    }

    @Nullable
    private Member[] getPlayerArray(){
        switch(gameCode){
            case "CHSS":
                game = Chess.getGame();
                return game.getMemberArray();
            case "LEEA":
                game = LeavingEarth.getGame();
                return game.getMemberArray();
            case "RISK":
                game = Risk.getGame();
                return game.getMemberArray();
            default:
                return null;
        }
    }

    //TODO: complete board game functionality
    //TODO: permanency for board game managers
}
