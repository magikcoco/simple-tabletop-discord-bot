package com.magikcoco.listeners;

import com.magikcoco.manager.*;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ThreadDeletionListener extends ListenerAdapter {

    private static LoggingManager lm = LoggingManager.getInstance();
    private static DataManager dm = DataManager.getInstance();

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        if(handleRemoveActiveThread(event)){
            lm.logInfo("I removed "+event.getChannel().getName()
                    +" of type "+event.getChannelType().toString()
                    +" as an active thread after it was deleted");
        } else {
            lm.logInfo("I saw that "+event.getChannel().getName()
                    +" of type "+event.getChannelType().toString()+" was deleted");
        }
    }

    /*
     * searches active threads to find the deleted one, then returns true if found and deleted or false otherwise
     */
    private boolean handleRemoveActiveThread(@NotNull ChannelDeleteEvent event){
        //chargen managers
        for(ChargenManager manager : dm.getActiveChargenManagers()){
            if(event.getChannel().equals(manager.getChargenThread())){
                dm.removeActiveManager(manager);
                return true;
            }
        }
        //board game managers
        for(BoardGameManager manager : dm.getActiveBoardGameMangers()){
            if(event.getChannel().equals(manager.getGameThread())){
                dm.removeActiveManager(manager);
                return true;
            }
        }
        //RPG managers
        for(RPGManager manager : dm.getActiveRPGMangers()){
            if(event.getChannel().equals(manager.getGameThread())){
                dm.removeActiveManager(manager);
                return true;
            }
        }
        return false;
    }
}
