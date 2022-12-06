package com.magikcoco.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextMessageListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter

    private final String BOT_NAME = "simple-tabletop-bot#2962"; //name of the bot includes tag

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //declarations
        User author = event.getAuthor(); //author of the message
        GuildMessageChannelUnion channel = event.getGuildChannel(); //channel in which the message was sent
        Message message = event.getMessage(); //the message sent
        //message for development purposes
        if(author.getAsTag().equals(BOT_NAME)){
            System.out.println(author.getAsTag() + " sent " + message.getContentRaw() + " which is of type "
                    + message.getType().toString() + " in channel '" + channel.getName() +"' which is of type "
                    + channel.getType().toString());
        } else {
            //we don't have the intent for this
            System.out.println(author.getAsTag() + " sent a message which is of type "
                    + message.getType().toString() + " in channel '" + channel.getName() +"' which is of type "
                    + channel.getType().toString());
        }
        //react to the message
        if(!messageHandled(author, channel, message)){
            //dont reply here
            System.out.println("I did not handle a message in " + channel.getName() + " from " + author.getAsTag());
        }
    }

    private boolean messageHandled(User author, GuildMessageChannelUnion channel, Message message){
        try{
            //restrict where threads are allowed as much as possible
            boolean threadAllowed = threadAllowedInChannel(channel);
            //checks for the message
            if(author.getAsTag().equals(BOT_NAME) && threadAllowed){
                //if the message was sent by this bot and is in a channel where threads can be made
                String content = message.getContentRaw();
                if(content.contains("CG")){
                    //this is a chargen thread
                    message.createThreadChannel(content).queue();
                }
            }
            //TODO: listen for threads that this bot creates
            return true;
        } catch(IllegalArgumentException e){
            System.out.println("There was an illegal argument exception trying to handle a message.");
        } catch(IllegalStateException e){
            //If the message's channel is not actually a IThreadContainer.
            System.out.println("There was an illegal state exception trying to handle a message.");
        } catch(UnsupportedOperationException e){
            //If this is a forum channel. You must use createForumPost(...) instead.
            System.out.println("There was an unsupported operation exception trying to handle a message.");
        } catch(InsufficientPermissionException e){
            //If this is a forum channel. You must use createForumPost(...) instead.
            System.out.println("There was an insufficient permission exception trying to handle a message.");
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
