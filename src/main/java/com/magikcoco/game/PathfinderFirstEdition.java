package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class PathfinderFirstEdition implements Game{
    private static PathfinderFirstEdition GAME = new PathfinderFirstEdition();
    
    public static Game getGame(){
        return GAME;
    }

    @Override
    public Member[] getMemberArray() {
        return new Member[5];
    }
}
