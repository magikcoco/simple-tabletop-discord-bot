package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class Chess implements Game {

    private static Chess GAME = new Chess();

    private Chess(){
        //default constructor
    }

    public static Game getGame(){
        return GAME;
    }

    public Member[] getMemberArray(){
        return new Member[2];
    }
}
