package com.magikcoco.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TextMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User author = event.getAuthor();
        GuildMessageChannelUnion channel = event.getGuildChannel();
        Message message = event.getMessage();
        try{
            if(author.getName().equals("simple-tabletop-bot") && channel.getType().toString().equals("TEXT")){
                String content = message.getContentRaw();
                if(content.contains("Create")){
                    //chargen thread
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
