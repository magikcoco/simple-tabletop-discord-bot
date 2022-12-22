package com.magikcoco.manager;

import com.magikcoco.game.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RPGManager implements Manager{

    private LoggingManager lm = LoggingManager.getInstance();
    private Member[] players;
    private ThreadChannel thread;
    private String gameCode;
    private Game game;
    private boolean autoGM;

    public RPGManager(@NotNull ThreadChannel thread){
        //set parameters
        this.thread = thread;
        //autoGM defaults to false
        autoGM = false;
        //get the game code
        gameCode = this.thread.getName().split(" ")[1];
        //get the appropriate player array
        players = getPlayerArray();
        if(game == null){
            //send message
            thread.sendMessage("/help for RPG commands\nsomething went wrong, please set the game").queue();
            //log error
            lm.logError("A problem occurred making RPGManager in thread '"
                    + thread.getName()+"' for game code '"+gameCode+"'");
        } else {
            thread.sendMessage("/help for RPG commands").queue();
            lm.logInfo("New RPGManager in thread '"
                    +this.thread.getName()
                    +"' for game code '"+gameCode+"'");
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

    public Member getGameMaster(){
        return players[0];
    }

    @Override
    public boolean addPlayer(Member newPlayer){
        int addIndex = -1;
        for(int i = 1; i < players.length; i++){
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

    public boolean addGM(Member newGM){
        if(players[0] == null){
            players[0] = newGM;
            lm.logInfo("Added "+newGM.getEffectiveName() + " as the GM in thread "+ thread.getName());
            return true;
        } else {
            lm.logInfo("Did not add "+newGM.getEffectiveName()+" as the GM in thread "+ thread.getName());
            return false;
        }
    }

    @Override
    public boolean removePlayer(Member player) {
        for(int i = 1; i<players.length;i++){
            if(players[i].equals(player)){
                players[i] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public Game getGame() {
        return null;
    }

    @Override
    public String getGameCode() {
        return gameCode;
    }

    public boolean hasAutoGM(){
        return autoGM;
    }

    @Nullable
    private Member[] getPlayerArray(){
        switch(gameCode){
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

    //TODO: complete TTRPG functionality
    //TODO: permanency for RPG managers
}
