package com.magikcoco.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextMessageListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter

    private final String BOT_NAME = "simple-tabletop-bot"; //name of the bot

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor(); //author of the message
        GuildMessageChannelUnion channel = event.getGuildChannel(); //channel in which the message was sent
        Message message = event.getMessage(); //the message sent
        try{
            if(author.getName().equals(BOT_NAME) && channel.getType().toString().equals("TEXT")){
                //if the message was sent by this bot and is in a text channel
                String content = message.getContentRaw();
                if(content.contains("Create")){
                    //the thread should be a chargen thread
                    message.createThreadChannel(content).queue();
                }
            }
        } catch(IllegalArgumentException e){
            //If the provided name is null, blank, empty, or longer than 100 characters
            message.reply("Cannot create thread. The provided name is null, blank, empty, or longer than 100 characters.");
        } catch(IllegalStateException e){
            //If the message's channel is not actually a IThreadContainer.
            message.reply("Cannot create thread. Threads are not possible here.");
        } catch(UnsupportedOperationException e){
            //If this is a forum channel. You must use createForumPost(...) instead.
            message.reply("Cannot create thread. This operation is not supported here.");
        } catch(InsufficientPermissionException e){
            //If this is a forum channel. You must use createForumPost(...) instead.
            message.reply("Cannot create thread. I don't have permission to do that.");
        }

    }
}
