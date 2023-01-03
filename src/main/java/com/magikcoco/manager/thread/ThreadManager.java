package com.magikcoco.manager.thread;

import com.magikcoco.game.*;
import com.magikcoco.manager.LoggingManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bson.Document;

public abstract class ThreadManager {

    protected LoggingManager lm = LoggingManager.getInstance();
    protected Member[] players = null; //the players for the game
    protected String gameCode = null; //the game code
    protected Game game = null; //the game object
    protected ThreadChannel thread = null; //the thread being managed
    protected Document document = null; //the mongodb document associated with this manager

    public boolean addPlayer(Member player){
        int addIndex = -1;
        for(int i = 0; i<players.length; i++){
            if(players[i].equals(player)){
                return false;
            } else if (players[i] == null){
                addIndex = i;
            }
        }
        if(addIndex > -1){
            players[addIndex] = player;
            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayer(Member player){
        for(int i = 0; i < players.length; i++){
            if(players[i].equals(player)){
                players[i] = null;
                return true;
            }
        }
        return false;
    }

    public Member[] getPlayers(){
        return players;
    }

    public String getGameCode(){
        return gameCode;
    }

    public Game getGame(){
        return game;
    }

    public ThreadChannel getThread(){
        return thread;
    }

    protected Member[] getPlayersArray(){
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
            case "HGRR":
                game = HouseGamesRevisedRules.getGame();
                return game.getMemberArray();
            case "PF1E":
                game = PathfinderFirstEdition.getGame();
                return game.getMemberArray();
            case "PFSP":
                game = PathfinderSpheres.getGame();
                return game.getMemberArray();
            case "SR5S":
                game = ShadowrunFifthSimplified.getGame();
                return game.getMemberArray();
            default:
                return null;
        }
    }
}
