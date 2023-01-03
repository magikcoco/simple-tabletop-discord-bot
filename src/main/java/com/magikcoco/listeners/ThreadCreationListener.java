package com.magikcoco.listeners;

import com.magikcoco.manager.*;
import com.magikcoco.manager.thread.BoardGameThreadManager;
import com.magikcoco.manager.thread.ChargenThreadManager;
import com.magikcoco.manager.thread.RPGThreadManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ThreadCreationListener extends ListenerAdapter {

    private static DataManager dm = DataManager.getInstance();
    private static LoggingManager lm = LoggingManager.getInstance();

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        if(addActiveManager(event)){
            lm.logInfo("Created a new manager to handle the thread");
        } else {
            lm.logInfo("Error adding manager to channel "+event.getChannel().getName());
        }
    }

    /*
     * Creates a new active manager for the thread where necessary
     */
    private boolean addActiveManager(@NotNull ChannelCreateEvent event){
        if(event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            if(event.getChannel().getName().contains("CG")){
                //chargen thread
                for(Member member : event.getGuild().getMembers()){
                    //need to find the correct member to pass to the chargenmanager
                    if(event.getChannel().getName().split(" ")[2].equals(member.getEffectiveName())){
                        dm.addActiveManager(new ChargenThreadManager(member, event.getChannel().asThreadChannel()));
                        return true;
                    }
                }
            } else if(event.getChannel().getName().contains("BG")){
                //board game thread
                dm.addActiveManager(new BoardGameThreadManager(event.getChannel().asThreadChannel()));
                return true;
            } else if(event.getChannel().getName().contains("RG")){
                //rpg thread
                dm.addActiveManager(new RPGThreadManager(event.getChannel().asThreadChannel()));
                return true;
            }
        }
        return false;
    }
}
