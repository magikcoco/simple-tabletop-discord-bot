package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class HouseGamesRevisedRules implements Game{
    private static HouseGamesRevisedRules GAME = new HouseGamesRevisedRules();

    public static Game getGame(){
        return GAME;
    }

    @Override
    public Member[] getMemberArray() {
        return new Member[5];
    }
}
