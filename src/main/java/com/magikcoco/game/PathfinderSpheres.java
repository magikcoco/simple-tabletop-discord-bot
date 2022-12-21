package com.magikcoco.game;

import net.dv8tion.jda.api.entities.Member;

public class PathfinderSpheres implements Game{
    private static PathfinderSpheres GAME = new PathfinderSpheres();
    
    public static Game getGame(){
        return GAME;
    }

    @Override
    public Member[] getMemberArray() {
        return new Member[5];
    }
}
