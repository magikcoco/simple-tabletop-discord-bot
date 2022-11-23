package com.magikcoco.eventlisteners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandEventListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch(event.getName()){
            case "ping":
                event.reply("pong").queue(); //always queue
                break;
            case "charcreate":
                handleCharCreate(event);
                break;
            default:
                break;
        }
    }

    private void handleCharCreate(SlashCommandInteractionEvent event){
        switch(event.getOption("Game").getAsString()){
            case "Cyberpunk 2020":
                //game shadowrun
                event.reply("Cyberpunk 2020 Selected but still WIP").queue();
                break;
            case "House Games":
                //game shadowrun
                event.reply("HouseGames Selected but still WIP").queue();
                break;
            case "Pathfinder":
                //game shadowrun
                event.reply("Pathfinder Selected but still WIP").queue();
                break;
            case "Shadowrun":
                //game shadowrun
                event.reply("Shadowrun Selected but still WIP").queue();
                break;
            default:
                event.reply("Unrecognized Game: " + event.getOption("Game").getAsString()
                        +"\n\nList of Supported Games:\n"
                        +"Cyberpunk 2020\n"
                        +"House Games\n"
                        +"Pathfinder\n"
                        +"Shadowrun\n").queue();
                break;
        }
    }
}
