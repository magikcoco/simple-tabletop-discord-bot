package com.magikcoco.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandEventListener extends ListenerAdapter {

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
        try {
            switch (event.getOption("game").getAsString()) {
                case "House Games":
                    //ttrpg House Games
                    event.reply("Create "+event.getOption("name").getAsString()
                            +" for "+event.getUser().getName()+" using House Games").queue();
                    break;
                case "Pathfinder":
                    //ttrpg Pathfinder
                    event.reply("Create "+event.getOption("name").getAsString()
                            +" for "+event.getUser().getName()+" using Pathfinder").queue();
                    break;
                case "Shadowrun 5S":
                    //ttrpg Shadowrun
                    event.reply("Create "+event.getOption("name").getAsString()
                            +" for "+event.getUser().getName()+" using Shadowrun 5S").queue();
                    break;
                case "Spheres of Power/Might":
                    //ttrpg Pathfinder
                    event.reply("Create "+event.getOption("name").getAsString()
                            +" for "+event.getUser().getName()+" using Spheres").queue();
                    break;
                default:
                    //in this case the ttrpg is unsupported
                    event.reply("Unrecognized Character Sheet: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Character Sheets:\n"
                            + "House Games(WIP)\n"
                            + "Pathfinder(WIP)\n"
                            + "Shadowrun 5S(WIP)\n"
                            + "Spheres of Power/Might(WIP)\n")
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
                    event.reply("House Games AutoDM WIP").queue();
                    break;
                case "Pathfinder":
                    //ttrpg Pathfinder
                    event.reply("Pathfinder AutoDM WIP").queue();
                    break;
                case "Shadowrun 5S":
                    //ttrpg Shadowrun
                    event.reply("Shadowrun AutoDM WIP").queue();
                    break;
                case "Spheres of Power/Might":
                    //ttrpg Pathfinder
                    event.reply("Spheres of Power/Might AutoDM WIP").queue();
                    break;
                default:
                    //in this case the game is unsupported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Games:\n"
                            + "House Games(WIP)\n"
                            + "Pathfinder(WIP)\n"
                            + "Shadowrun 5S(WIP)\n"
                            + "Spheres of Power/Might(WIP)\n").queue();
                    break;
            }
        } catch (NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }
}
