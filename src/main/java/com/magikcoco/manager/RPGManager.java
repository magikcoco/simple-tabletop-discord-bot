package com.magikcoco.manager;

import com.magikcoco.game.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RPGManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private Member[] players;
    private Member gameMaster;
    private ThreadChannel gameThread;
    private String gameCode;
    private Game game;
    private boolean autoGM;

    public RPGManager(@NotNull ThreadChannel gameThread){
        this.gameThread = gameThread;
        autoGM = false;
        gameCode = this.gameThread.getName().split(" ")[1];
        players = getPlayerArray();
        if(game == null){
            gameThread.sendMessage("/help for RPG commands\nsomething went wrong, please set the game").queue();
            lm.logError("A problem occurred making RPGManager in thread '"
                    +gameThread.getName()+"' for game code '"+gameCode+"'");
        } else {
            gameMaster = null;
            gameThread.sendMessage("/help for RPG commands").queue();
            lm.logInfo("New RPGManager in thread '"
                    +this.gameThread.getName()
                    +"' for game code '"+gameCode+"'");
        }
    }

    public Member[] getPlayers(){
        return players;
    }

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
            lm.logInfo("Added "+newGM.getEffectiveName() + " as the GM in thread "+gameThread.getName());
            return true;
        } else {
            lm.logInfo("Did not add "+newGM.getEffectiveName()+" as the GM in thread "+gameThread.getName());
            return false;
        }
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
