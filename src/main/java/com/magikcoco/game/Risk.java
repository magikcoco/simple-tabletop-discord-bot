package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class Risk implements Game {

    private static Risk GAME = new Risk();

    private Risk(){
        //default constructor
    }

    public static Risk getGame(){
        return GAME;
    }

    public Member[] getMemberArray(){
        return new Member[5];
    }
}
