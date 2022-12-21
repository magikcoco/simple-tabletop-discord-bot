package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class LeavingEarth implements Game {

    private static LeavingEarth GAME = new LeavingEarth();

    private LeavingEarth(){
        //default constructor
    }

    public static Game getGame(){
        return GAME;
    }

    public Member[] getMemberArray(){
        return new Member[5];
    }
}
