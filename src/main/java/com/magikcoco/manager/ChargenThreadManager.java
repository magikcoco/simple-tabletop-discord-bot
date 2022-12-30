package com.magikcoco.manager;

import com.magikcoco.game.Game;
import com.magikcoco.game.HouseGamesRevisedRules;
import com.magikcoco.game.PathfinderFirstEdition;
import com.magikcoco.game.PathfinderSpheres;
import com.magikcoco.game.ShadowrunFifthSimplified;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChargenThreadManager implements ThreadManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private Member player; //the person making the character
    private ThreadChannel thread; //the thread for making the character
    private String gameCode; //the game code
    private Game game; //the game object

    public ChargenThreadManager(Member player, @NotNull ThreadChannel thread){
        //set passed parameters
        this.player = player;
        this.thread = thread;
        //add the player to the thread
        thread.addThreadMember(player).queue();
        //get the game code
        gameCode = thread.getName().split(" ")[1];
        //set the game value
        game = setGame();
        if (game == null) {
            //send a message in the thread
            thread.sendMessage("/help for chargen commands\nSomething went wrong, please set the game").queue();
            //log information
            lm.logError("New Chargen ThreadManager made in thread '"
                    +this.thread.getName()
                    +"' for user '"+this.player.getEffectiveName()
                    +"' but the game has not been set");
        } else {
            //send a message in the thread
            thread.sendMessage("/help for chargen commands").queue();
            //log information
            lm.logInfo("New Chargen ThreadManager made in thread '"
                    +this.thread.getName()
                    +"' for user '"+this.player.getEffectiveName()
                    +"' and game code '"+ gameCode);
        }
    }

    @Override
    public ThreadChannel getThread() {
        return thread;
    }

    @Override
    public Member[] getPlayers() {
        return new Member[]{player};
    }

    @Override
    public boolean addPlayer(Member player) {
        if(this.player != null){
            return false;
        } else {
            this.player = player;
            return true;
        }
    }

    @Override
    public boolean removePlayer(Member player) {
        if(this.player != player){
            return false;
        } else {
            this.player = null;
            return true;
        }
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
    private Game setGame(){
        return switch (gameCode) {
            case "HGRR" -> HouseGamesRevisedRules.getGame();
            case "PF1E" -> PathfinderFirstEdition.getGame();
            case "PFSP" -> PathfinderSpheres.getGame();
            case "SR5S" -> ShadowrunFifthSimplified.getGame();
            default -> null;
        };
    }
    //TODO: complete chargen functionality
    //TODO: permanency for chargen managers
}
