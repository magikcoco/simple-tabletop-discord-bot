package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class ShadowrunFifthSimplified implements Game{
    private static ShadowrunFifthSimplified GAME = new ShadowrunFifthSimplified();
    
    public static Game getGame(){
        return GAME;
    }

    @Override
    public Member[] getMemberArray() {
        return new Member[5];
    }
}
