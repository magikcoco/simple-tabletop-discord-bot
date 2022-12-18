package com.magikcoco.manager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ChargenManager {

    private LoggingManager lm = LoggingManager.getInstance();
    private Member charOwner; //the person making the character
    private ThreadChannel chargenThread; //the thread for making the character
    private String chargenGame;

    public ChargenManager(Member charOwner, ThreadChannel chargenThread){
        this.charOwner = charOwner;
        this.chargenThread = chargenThread;
        chargenGame = chargenThread.getName().split(" ")[1];
        chargenThread.sendMessage("/help for chargen commands").queue();
        lm.logInfo("New Chargen Manager made in thread '"
                +this.chargenThread.getName()
                +"' for user '"+this.charOwner.getEffectiveName()
                +"' and game code '"+chargenGame);
    }

    public Member getCharOwner() {
        return charOwner;
    }

    public ThreadChannel getChargenThread(){
        return chargenThread;
    }

    //TODO: complete chargen functionality
    //TODO: permanency for chargen managers
}
