package com.magikcoco.listeners;

import com.magikcoco.bot.Bot;
import com.magikcoco.manager.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
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
            if(event.getAuthor().getAsTag().equals(botName) && slashCommandThreadResponse(event)){
                lm.logInfo("Slash command response '"+event.getMessage().getContentRaw()+"' was handled");
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
            if(content.contains("CG")){
                //chargen thread case
                if(chargenThreadAlreadyExists(event.getMessage())){
                    String[] threadInfo = event.getMessage().getContentRaw().split(" ");
                    //when response first comes this should always be:
                    //0 is thread type code
                    //1 is code for the game
                    //2 is the name of the char owner
                    for(ChargenManager manager : dm.getActiveChargenManagers()){
                        if(manager.getPlayers()[0].getEffectiveName().equals(threadInfo[2]) && manager.getGameCode().equals(threadInfo[1])){
                            manager.getThread().sendMessage(Objects.requireNonNull(event.getMessage().getInteraction()).getUser().getAsMention()).queue();
                        }
                    }
                } else {
                    event.getMessage().createThreadChannel(content).queue();
                }
                return true;
            } else if(content.contains("BG") || content.contains("RG")) {
                event.getMessage().createThreadChannel(content).queue();
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
        String[] threadInfo = message.getContentRaw().split(" ");
        //when response first comes this should always be:
        //0 is thread type code
        //1 is code for the game
        //2 is the name of the char owner
        for(ChargenManager manager : dm.getActiveChargenManagers()){
            if(manager.getPlayers()[0].getEffectiveName().equals(threadInfo[2])
                    && manager.getGameCode().equals(threadInfo[1])){
                return true;
            }
        }
        return false;
    }

    /*
     * checks if the message should be responded to with a thread
     */
    private boolean needsThreadResponse(@NotNull MessageReceivedEvent event){
        String content = event.getMessage().getContentRaw();
        return event.getMessage().getType().equals(MessageType.SLASH_COMMAND)
                && threadAllowedInChannel(event.getGuildChannel())
                && (content.contains("CG") || content.contains("BG") || content.contains("RG"));
    }
}
