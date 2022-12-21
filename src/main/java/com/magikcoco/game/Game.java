package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public interface Game {
    static Game getGame() {
        return null;
    }

    Member[] getMemberArray();
}
