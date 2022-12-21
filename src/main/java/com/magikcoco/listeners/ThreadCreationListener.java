package com.magikcoco.listeners;

import com.magikcoco.manager.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadCreationListener extends ListenerAdapter {

    private static DataManager dm = DataManager.getInstance();
    private static LoggingManager lm = LoggingManager.getInstance();

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        if(addActiveManager(event)){
            lm.logInfo("Created a new manager to handle the thread");
            if(threadStarted(event)){
                lm.logInfo("Successfully handled creation of thread called "+event.getChannel().getName());
            } else {
                lm.logInfo("Did not handle creation of thread called "+event.getChannel().getName());
            }
        } else {
            lm.logInfo("No new manager was created for channel "+event.getChannel().getName());
        }
    }

    /*
     * Creates a new active manager for the thread where necessary
     */
    private boolean addActiveManager(@NotNull ChannelCreateEvent event){
        if(event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            if(event.getChannel().getName().contains("CG")){
                for(Member member : event.getGuild().getMembers()){
                    if(event.getChannel().getName().split(" ")[2].equals(member.getEffectiveName())){
                        event.getChannel().asThreadChannel().addThreadMember(member).queue();
                        dm.addActiveManager(new ChargenManager(member, event.getChannel().asThreadChannel()));
                        return true;
                    }
                }
            } else if(event.getChannel().getName().contains("BG")){
                dm.addActiveManager(new BoardGameManager(event.getChannel().asThreadChannel()));
                return true;
            } else if(event.getChannel().getName().contains("RG")){
                dm.addActiveManager(new RPGManager(event.getChannel().asThreadChannel()));
                return true;
            }
        }
        return false;
    }

    /*
     * Handles sending a message inside a thread that was just created
     */
    private boolean threadStarted(@NotNull ChannelCreateEvent event){
            if (canSendMessagesInThread(event)) { //check if the bot can put messages in the thread
                ChargenManager chargenManager = getIfChargenThread(event);
                BoardGameManager boardGameManager = getIfBoardGameThread(event);
                RPGManager rpgManager = getIfRPGThread(event);
                if(!(chargenManager == null)) {
                    if(chargenManager.getChargenThread().equals(event.getChannel().asThreadChannel())){
                        //add a member to the thread just created
                        for (Member member : event.getGuild().getMembers()) {
                            if (member.equals(chargenManager.getCharOwner())) {
                                event.getChannel().asThreadChannel().addThreadMember(member).queue();
                                return true;
                            }
                        }
                    }
                } else if(!(boardGameManager == null)) {
                    if(boardGameManager.getGameThread().equals(event.getChannel().asThreadChannel())){
                        event.getChannel().asThreadChannel().sendMessage("/help for game commands").queue();
                        return true;
                    }
                } else if(!(rpgManager == null)) {
                    if(rpgManager.getGameThread().equals(event.getChannel().asThreadChannel()))
                    event.getChannel().asThreadChannel().sendMessage("/help for RPG commands").queue();
                    return true;
                }
            }
            return false;
    }

    /*
     * checks if the thread a message just created can have messages sent by this bot
     */
    private boolean canSendMessagesInThread(@NotNull ChannelCreateEvent event){
        return event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)
                && (event.getChannel().asThreadChannel().isJoined() //the thread is joined
                && !event.getChannel().asThreadChannel().isLocked() //the thread is not locked
                && !event.getChannel().asThreadChannel().isArchived()); //the thread is not archived
    }

    /*
     * return the chargen manager for this thread, or null is there is none
     */
    @Nullable
    private ChargenManager getIfChargenThread(@NotNull ChannelCreateEvent event){
        for(ChargenManager manager : dm.getActiveChargenManagers()){
            if(event.getChannel().equals(manager.getChargenThread())){
                return manager;
            }
        }
        return null;
    }

    /*
     * return the board game manager for this thread, or null if there is none
     */
    @Nullable
    private BoardGameManager getIfBoardGameThread(@NotNull ChannelCreateEvent event){
        for(BoardGameManager manager : dm.getActiveBoardGameMangers()){
            if(event.getChannel().equals(manager.getGameThread())){
                return manager;
            }
        }
        return null;
    }

    /*
     * return the rpg manager for this thread, or null if there is none
     */
    @Nullable
    private RPGManager getIfRPGThread(@NotNull ChannelCreateEvent event){
        for(RPGManager manager : dm.getActiveRPGMangers()){
            if(event.getChannel().equals(manager.getGameThread())){
                return manager;
            }
        }
        return null;
    }
}
