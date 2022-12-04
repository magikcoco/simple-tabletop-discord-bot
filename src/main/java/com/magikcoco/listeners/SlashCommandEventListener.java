package com.magikcoco.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandEventListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //listens for slashcommands
        switch(event.getName()){
            case "ping":
                //ping slash command, for testing purposes
                event.reply("pong").queue(); //always queue
                break;
            case "charcreate":
                //charcreate slash command, used to initiate character creation process
                handleCharCreate(event);
                break;
            case "startboardgame":
                //startboardgame slash command, used to initiate a board game
                handleStartBoardGame(event);
                break;
            case "startttrpg":
                //startttrpg slash command, used to initiate a ttrpg
                handleStartTTRPG(event);
                break;
            default:
                //default case is an unrecognized command
                break;
        }
    }

    private void handleCharCreate(SlashCommandInteractionEvent event){
        //all replies must be under 100 characters including name, which is always between 2 and 32 characters
        try {
            switch (event.getOption("game").getAsString()) {
                case "House Games":
                    //ttrpg House Games, "House Games Revised Rules"
                    event.reply("CG HGRR "+event.getUser().getAsTag()).queue();
                    break;
                case "Pathfinder 1e":
                    //ttrpg Pathfinder, "Pathfinder First Edition"
                    event.reply("CG PF1E "+event.getUser().getAsTag()).queue();
                    break;
                case "Pathfinder Spheres":
                    //ttrpg Pathfinder, "Pathfinder Spheres of Power/Might"
                    event.reply("CG PFSP "+event.getUser().getAsTag()).queue();
                    break;
                case "Shadowrun 5S":
                    //ttrpg Shadowrun, "Shadowrun 5th Edition Simplified"
                    event.reply("CG SR5S "+event.getUser().getAsTag()).queue();
                    break;
                default:
                    //in this case the ttrpg is unsupported
                    event.reply("Unrecognized Character Sheet: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Character Sheets:\n"
                            + "House Games(WIP)\n"
                            + "Pathfinder 1e(WIP)\n"
                            + "Pathfinder Spheres(WIP)\n"
                            + "Shadowrun 5S(WIP)\n")
                            .queue();
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
                    //in this case the board game is not supported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Board Games:\n"
                            + "Leaving Earth(WIP)\n").queue();
                    break;
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }

    private void handleStartTTRPG(SlashCommandInteractionEvent event){
        try{
            switch (event.getOption("game").getAsString()) {
                case "House Games":
                    //ttrpg House Games
                    event.reply("HGRR AutoDM WIP").queue();
                    break;
                case "Pathfinder 1e":
                    //ttrpg Pathfinder
                    event.reply("PF1E AutoDM WIP").queue();
                    break;
                case "Pathfinder Spheres":
                    //ttrpg Pathfinder
                    event.reply("PFSP AutoDM WIP").queue();
                    break;
                case "Shadowrun 5S":
                    //ttrpg Shadowrun
                    event.reply("SR5S AutoDM WIP").queue();
                    break;
                default:
                    //in this case the game is unsupported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Games:\n"
                            + "House Games(WIP)\n"
                            + "Pathfinder 1e(WIP)\n"
                            + "Pathfinder Spheres(WIP)\n"
                            + "Shadowrun 5S(WIP)\n")
                            .queue();
                    break;
            }
        } catch (NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }
}
