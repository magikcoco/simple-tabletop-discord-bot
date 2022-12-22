package com.magikcoco.manager;

import com.magikcoco.game.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoardGameManager implements Manager{

    private LoggingManager lm = LoggingManager.getInstance();
    private Member[] players; //the players for the game
    private ThreadChannel thread; //the thread where the game is played
    private String gameCode; //the game code
    private Game game; //the game object

    public BoardGameManager(@NotNull ThreadChannel thread){
        //set passed parameters
        this.thread = thread;
        //get the game code
        gameCode = thread.getName().split(" ")[1];
        //get the array of players
        players = getPlayerArray();
        if(game == null){
            //send a message
            thread.sendMessage("/help for game commands\nsomething went wrong, please set the game").queue();
            //log the error
            lm.logError("A problem occurred making BoardGameManager in thread '"
                    + thread.getName()+"' for game code '"+gameCode+"'");
        } else {
            //send a message
            thread.sendMessage("/help for game commands").queue();
            //log info
            lm.logInfo("New BoardGameManger in thread '"
                    + thread.getName() +"' for game code '" + gameCode + "'");
        }
    }

    @Override
    public ThreadChannel getThread() {
        return thread;
    }

    @Override
    public Member[] getPlayers(){
        return players;
    }

    @Override
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

    @Override
    public boolean removePlayer(Member player) {
        for(int i = 0; i<players.length;i++){
            if(players[i].equals(player)){
                players[i] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public String getGameCode() {
        return gameCode;
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
