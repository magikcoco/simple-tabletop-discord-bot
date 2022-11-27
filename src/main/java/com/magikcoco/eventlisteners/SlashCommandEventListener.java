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
            case "startboardgame":
                handleStartBoardGame(event);
                break;
            default:
                break;
        }
    }

    private void handleCharCreate(SlashCommandInteractionEvent event){
        try {
            switch (event.getOption("game").getAsString()) {
                case "House Games":
                    //ttrpg House Games
                    event.reply("House Games WIP").queue();
                    break;
                case "Pathfinder":
                    //ttrpg Pathfinder
                    event.reply("Pathfinder WIP").queue();
                    break;
                case "Shadowrun 5S":
                    //ttrpg Shadowrun
                    event.reply("Shadowrun WIP").queue();
                    break;
                case "Spheres of Power/Might":
                    //ttrpg Pathfinder
                    event.reply("Spheres of Power/Might WIP").queue();
                    break;
                default:
                    //in this case the game is unsupported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Games With Character Sheets:\n"
                            + "House Games(WIP)\n"
                            + "Pathfinder(WIP)\n"
                            + "Shadowrun 5S(WIP)\n"
                            + "Spheres of Power/Might(WIP)\n").queue();
                    break;
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }

    private void handleStartBoardGame(SlashCommandInteractionEvent event){
        try {
            switch (event.getOption("game").getAsString()) {
                case "Leaving Earth":
                    //board game Leaving Earth
                    event.reply("Leaving Earth WIP").queue();
                    break;
                default:
                    //in this case the game is not supported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Board Games:\n"
                            + "Leaving Earth(WIP)\n").queue();
                    break;
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }
}
