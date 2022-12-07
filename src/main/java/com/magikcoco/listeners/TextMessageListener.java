package com.magikcoco.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.List;

public class TextMessageListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter
    //TODO: move souts to log output file

    private final String BOT_NAME = "simple-tabletop-bot#2962"; //name of the bot includes tag

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //declarations
        User author = event.getAuthor(); //author of the message
        GuildMessageChannelUnion channel = event.getGuildChannel(); //channel in which the message was sent
        Message message = event.getMessage(); //the message sent
        /*uncomment this section get information on whats being sent

        if(author.getAsTag().equals(BOT_NAME)){
            System.out.println(author.getAsTag() + " sent '" + message.getContentRaw() + "' which is of type "
                    + message.getType().toString() + " in channel '" + channel.getName() +"' which is of type "
                    + channel.getType().toString());
        } else {
            //we don't have the intent for this
            System.out.println(author.getAsTag() + " sent a message which is of type "
                    + message.getType().toString() + " in channel '" + channel.getName() +"' which is of type "
                    + channel.getType().toString());
        }*/
        //react to the message
        if(!messageHandled(author, channel, message)){
            //dont reply here
            System.out.println("I did not handle a message in " + channel.getName() + " from " + author.getAsTag());
        }
    }

    private boolean messageHandled(User author, GuildMessageChannelUnion channel, Message message){
        try{
            //checks for the message
            if(author.getAsTag().equals(BOT_NAME)){
                if(slashCommandThreadResponse(message, channel)){
                    System.out.println("A slash command response that needed a thread was handled");
                } else if(threadStarted(message, channel)){
                    System.out.println("I added someone to a thread");
                }
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean slashCommandThreadResponse(Message message, GuildMessageChannelUnion channel){
        boolean threadAllowed = threadAllowedInChannel(channel);
        String content = message.getContentRaw();
        if(message.getType().equals(MessageType.SLASH_COMMAND) && threadAllowed){
            //we are only replying to slash commands with threads here
            if(content.contains("CG")){
                //create a chargen thread
                List<ThreadChannel> threadChannels = message.getChannel().asThreadContainer().getThreadChannels();
                ThreadChannel existingThread = null;
                boolean exists = false;
                for(ThreadChannel tc : threadChannels){
                    if(tc.getName().equals(content)){
                        exists = true;
                        existingThread = tc;
                    }
                }
                if(!exists){
                    message.createThreadChannel(content).queue();
                } else {
                    //TODO: figure out why user that should be a thread member is not
                    for(ThreadMember member : existingThread.getThreadMembers()){
                        System.out.println(member.getMember().getEffectiveName());
                    }
                }
                return true;
            } //TODO: add cases for other commands
        }
        return false;
    }

    private boolean threadStarted(Message message, GuildMessageChannelUnion channel){
        if(message.getType().equals(MessageType.THREAD_STARTER_MESSAGE) //the message is a thread starter message
                && message.getChannel().asThreadChannel().isJoined() //the thread is joined
                && !message.getChannel().asThreadChannel().isLocked()){ //the thread is not locked
            if(message.getChannel().getName().contains("CG")){ //the thread made is a chargen thread
                //add a member to the thread just created
                for(Member member : channel.getGuild().getMembers()){
                    if(message.getChannel().getName().split(" ")[2].equals(member.getEffectiveName())){
                        message.getChannel().asThreadChannel().addThreadMember(member).queue();
                        //message.getChannel().sendMessage(member.getAsMention()).queue(); how to mention someone
                        return true;
                    }
                }
            } //TODO: add cases for other commands
        }
        return false;
    }

    private boolean threadAllowedInChannel(GuildMessageChannelUnion channel){
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
}
