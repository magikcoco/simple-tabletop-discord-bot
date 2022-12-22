package com.magikcoco.manager;

import com.magikcoco.game.Game;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public interface Manager {
    ThreadChannel getThread();
    Member[] getPlayers();
    boolean addPlayer(Member player);
    boolean removePlayer(Member player);
    Game getGame();
    String getGameCode();
}
