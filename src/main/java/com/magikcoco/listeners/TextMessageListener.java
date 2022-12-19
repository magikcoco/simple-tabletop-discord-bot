package com.magikcoco.listeners;

import com.magikcoco.bot.Bot;
import com.magikcoco.manager.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class TextMessageListener extends ListenerAdapter {

    private String botName;
    private static LoggingManager lm = LoggingManager.getInstance();
    private static DataManager dm = DataManager.getInstance();

    public TextMessageListener() {
        botName = Bot.getInstance().getBotName();
        lm.logInfo("TextMessageListener has been created");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().getAsTag().equals(botName)){
            lm.logInfo(event.getAuthor().getAsTag() + " sent '" + event.getMessage().getContentRaw()
                    + "' which is of type " + event.getMessage().getType().toString() + " in channel '"
                    + event.getGuildChannel().getName() +"' which is of type "
                    + event.getGuildChannel().getType().toString());
        } else {
            //we don't have the intent for this
            lm.logInfo(event.getAuthor().getAsTag() + " sent a message which is of type "
                    + event.getMessage().getType().toString() + " in channel '" + event.getGuildChannel().getName()
                    + "' which is of type " + event.getGuildChannel().getType().toString());
        }

        //react to the message
        if(!messageHandled(event)){
            //dont reply here
            lm.logInfo("I did not handle a message in " + event.getGuildChannel().getName() + " from " + event.getAuthor().getAsTag());
        }
    }

    /*
     * handles the MessageReceivedEvent that just came in
     */
    private boolean messageHandled(MessageReceivedEvent event){
        try{
            if(event.getAuthor().getAsTag().equals(botName)){
                if(slashCommandThreadResponse(event)){
                    lm.logInfo("Slash command response '"+event.getMessage().getContentRaw()+"' was handled");
                } else if(threadStarted(event)){
                    lm.logInfo("I sent a message in a thread called "+event.getMessage().getChannel().getName());
                }
            }
            return true;
        } catch(Exception e) {
            lm.logError("There was an error handling an incoming message:\n"+e.toString());
        }
        return false;
    }

    /*
     * Handles responding to a slash command message. Returns false if it did nothing, true otherwise.
     */
    private boolean slashCommandThreadResponse(@NotNull MessageReceivedEvent event){
        if(needsThreadResponse(event)){
            //we only want to reply to slash command responses in this case
            String content = event.getMessage().getContentRaw(); //the content of the message that caused the event
            if(!content.contains("CG")){
                event.getMessage().createThreadChannel(content).queue();
                return true;
            } else {
                //we only care about it already existing if it's a chargen thread
                if(!chargenThreadAlreadyExists(event.getMessage())){
                    event.getMessage().createThreadChannel(content).queue();
                } else { //in this case the thread exists, and handling it depends on it's purpose
                    //if the chargen thread already exists, ping the user
                    String memberName = event.getMessage().getContentRaw().split(" ")[2];
                    for(ChargenManager manager : dm.getActiveChargenManagers()){
                        if(manager.getCharOwner().getEffectiveName().equals(memberName)){
                            manager.getChargenThread().sendMessage(Objects.requireNonNull(event.getMessage().getInteraction()).getUser().getAsMention()).queue();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /*
     * Handles sending a message inside a thread that was just created
     */
    private boolean threadStarted(@NotNull MessageReceivedEvent event){
        if(canSendMessagesInThread(event.getMessage())){ //check if the bot can put messages in the thread
            if(event.getMessage().getChannel().getName().contains("CG")){ //chargen thread case
                //add a member to the thread just created
                for(Member member : event.getMessage().getGuild().getMembers()){
                    if(event.getMessage().getChannel().getName().split(" ")[2].equals(member.getEffectiveName())){
                        event.getMessage().getChannel().asThreadChannel().addThreadMember(member).queue();
                        dm.addActiveManager(new ChargenManager(member, event.getChannel().asThreadChannel()));
                        return true;
                    }
                }
            } else if(event.getMessage().getChannel().getName().contains("BG")){ //the thread is a board game thread
                dm.addActiveManager(new BoardGameManager(new ArrayList<>(),event.getChannel().asThreadChannel()));
            } else if(event.getMessage().getChannel().getName().contains("RG")){ //the thread is a TTRPG thread
                dm.addActiveManager(new RPGManager(new ArrayList<>(),event.getChannel().asThreadChannel()));
                return true;
            }
        }
        return false;
    }

    /*
     * Returns whether or not the given channel can have threads in it
     */
    private boolean threadAllowedInChannel(@NotNull GuildMessageChannelUnion channel){
        return channel.getType().equals(ChannelType.TEXT)
                && !channel.getType().equals(ChannelType.GUILD_PUBLIC_THREAD)
                && !channel.getType().equals(ChannelType.GUILD_PRIVATE_THREAD)
                && !channel.getType().equals(ChannelType.GUILD_NEWS_THREAD)
                && !channel.getType().equals(ChannelType.PRIVATE)
                && !channel.getType().equals(ChannelType.FORUM)
                && !channel.getType().equals(ChannelType.VOICE)
                && !channel.getType().equals(ChannelType.STAGE)
                && !channel.getType().equals(ChannelType.NEWS)
                && !channel.getType().equals(ChannelType.GROUP)
                && !channel.getType().equals(ChannelType.CATEGORY)
                && !channel.getType().equals(ChannelType.UNKNOWN);
    }

    /*
     * Checks if the given message content matches any thread titles, returns true if yes or false otherwise
     */
    private boolean chargenThreadAlreadyExists(@NotNull Message message){
        String memberName = message.getContentRaw().split(" ")[2];
        for(ChargenManager manager : dm.getActiveChargenManagers()){
            if(manager.getCharOwner().getEffectiveName().equals(memberName)){
                return true;
            }
        }
        return false;
    }

    /*
     * Checks if the message contains certain keywords that indicate a thread response is needed
     */
    private boolean threadResponseNeeded(@NotNull Message message){
        String content = message.getContentRaw();
        return content.contains("CG") || content.contains("BG") || content.contains("RG");
    }

    /*
     * checks if the thread a message just created can have messages sent by this bot
     */
    private boolean canSendMessagesInThread(@NotNull Message message){
        return message.getType().equals(MessageType.THREAD_STARTER_MESSAGE) //the message is a thread starter message
                && message.getChannel().asThreadChannel().isJoined() //the thread is joined
                && !message.getChannel().asThreadChannel().isLocked() //the thread is not locked
                && !message.getChannel().asThreadChannel().isArchived(); //the thread is not archived
    }

    /*
     * checks if the message should be responded to with a thread
     */
    private boolean needsThreadResponse(@NotNull MessageReceivedEvent event){
        return event.getMessage().getType().equals(MessageType.SLASH_COMMAND)
                && threadAllowedInChannel(event.getGuildChannel())
                && threadResponseNeeded(event.getMessage());
    }

    //TODO: move thread responses to it's own listener
}
