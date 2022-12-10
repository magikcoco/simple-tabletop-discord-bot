package com.magikcoco.listeners;

import com.magikcoco.bot.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

public class TextMessageListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter
    //TODO: #7 move souts to log output file

    private String botName;

    public TextMessageListener() {
        botName = Bot.getInstance().getBotName();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        /*uncomment this section get information on whats being sent*/
        if(event.getAuthor().getAsTag().equals(botName)){
            System.out.println(event.getAuthor().getAsTag() + " sent '" + event.getMessage().getContentRaw() + "' which is of type "
                    + event.getMessage().getType().toString() + " in channel '" + event.getGuildChannel().getName() +"' which is of type "
                    + event.getGuildChannel().getType().toString());
        } else {
            //we don't have the intent for this
            System.out.println(event.getAuthor().getAsTag() + " sent a message which is of type "
                    + event.getMessage().getType().toString() + " in channel '" + event.getGuildChannel().getName() +"' which is of type "
                    + event.getGuildChannel().getType().toString());
        }
        /*uncomment this section get information on whats being sent*/

        //react to the message
        if(!messageHandled(event)){
            //dont reply here
            System.out.println("I did not handle a message in " + event.getGuildChannel().getName() + " from " + event.getAuthor().getAsTag());
        }
    }

    /*
     * handles the MessageReceivedEvent that just came in
     */
    private boolean messageHandled(MessageReceivedEvent event){
        try{
            if(event.getAuthor().getAsTag().equals(botName)){
                if(slashCommandThreadResponse(event)){
                    System.out.println("A slash command response that needed a thread was handled");
                } else if(threadStarted(event)){
                    System.out.println("I added someone to a thread");
                }
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
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
            if(!threadAlreadyExists(event.getMessage())){ //if the thread doesnt exist create it
                event.getMessage().createThreadChannel(content).queue();
                return true;
            } else { //in this case the thread exists, and handling it depends on it's purpose
                if(content.contains("CG")){ //this is a chargen thread
                    //if the chargen thread already exists, ping the user
                    try{
                        for(ThreadChannel tc : event.getChannel().asThreadContainer().getThreadChannels()){
                            if(tc.getName().equals(event.getMessage().getContentRaw())){
                                tc.sendMessage(Objects.requireNonNull(event.getMessage().getInteraction()).getUser().getAsMention()).queue();
                            }
                        }
                        return true;
                    } catch(NullPointerException e){
                        //something went wrong here
                        System.out.println("I encountered a NullPointerException handling message '"+event.getMessage().getContentRaw()+"'");
                        return false;
                    }
                } else if(content.contains("BG")){ //this is a board game thread
                    //if the board game thread already exists, check game over and participants first, and then ping
                    //TODO: #5 Handle a board game thread that already exists (needs #2 TODO first)
                    return true;
                } else if(content.contains("RG")){ //this is a TTRPG thread
                    //if the TTRPG thread already exists, check game over and participants first, and then ping
                    //TODO: #6 Handle a TTRPG thread that already exists (needs #3 TODO first)
                    return true;
                }
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
                        //TODO: #4 chargen manager
                        return true;
                    }
                }
            } else if(event.getMessage().getChannel().getName().contains("BG")){ //the thread is a board game thread
                event.getMessage().getChannel().asThreadChannel().sendMessage("Please specify the players for this game").queue();
                //TODO: #2 board game manager
            } else if(event.getMessage().getChannel().getName().contains("RG")){ //the thread is a TTRPG thread
                event.getMessage().getChannel().asThreadChannel().sendMessage("AutoDM is disabled\n"
                        +"Please specify the players and DM\n").queue();
                //TODO: #3 RPG manager and autoDM
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
    private boolean threadAlreadyExists(@NotNull Message message){
        String content = message.getContentRaw();
        //get a list of all the thread channels in the channel
        List<ThreadChannel> threadChannels = message.getChannel().asThreadContainer().getThreadChannels();
        boolean exists = false; //is this thread already exists
        for(ThreadChannel tc : threadChannels){ //go through all the threads
            if(tc.getName().equals(content)){ //if the thread has the same name it already exists
                exists = true;
            }
        }
        return exists;
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
}
