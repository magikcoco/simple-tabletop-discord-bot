package com.magikcoco.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandEventListener extends ListenerAdapter {
    //every listener class must extend ListenerAdapter

    //declarations
    //options for character sheets
    private String[] charCreateOptions = new String[]{"house games","pathfinder 1e","pathfinder spheres","shadowrun 5s"};
    //options for board game start
    private String[] startBoardGameOptions = new String[]{"leaving earth", "risk", "chess"};
    //options for AutoDM
    private String[] startTtrpgOptions = new String[]{"house games","pathfinder 1e","pathfinder spheres","shadowrun 5s"};


    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("charcreate") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(charCreateOptions)
                    .filter(charCreateOptions->charCreateOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(charCreateOptions->new Command.Choice(charCreateOptions,charCreateOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        } else if(event.getName().equals("startboardgame") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(startBoardGameOptions)
                    .filter(startBoardGameOptions->startBoardGameOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(startBoardGameOptions->new Command.Choice(startBoardGameOptions,startBoardGameOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        } else if(event.getName().equals("startttrpg") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(startTtrpgOptions)
                    .filter(startTtrpgOptions->startTtrpgOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(startTtrpgOptions->new Command.Choice(startTtrpgOptions,startTtrpgOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        //listens for slashcommands
        switch(event.getName()){
            case "help":
                //help slash command, for explaining purposes
                event.reply("/ping - replies with pong\n"
                        +"/charcreate [game] - starts a thread to create a character for the specified game\n"
                        +"/startboardgame [game] - starts a thread for playing a board game\n"
                        +"/startttrpg [game] - starts a thread for playing a ttrpg with\n").queue();
                break;
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
            switch (event.getOption("game").getAsString().toLowerCase()) {
                case "house games":
                    //ttrpg House Games, "House Games Revised Rules"
                    event.reply("CG HGRR "+event.getUser().getName()).queue();
                    break;
                case "pathfinder 1e":
                    //ttrpg Pathfinder, "Pathfinder First Edition"
                    event.reply("CG PF1E "+event.getUser().getName()).queue();
                    break;
                case "pathfinder spheres":
                    //ttrpg Pathfinder, "Pathfinder Spheres of Power/Might"
                    event.reply("CG PFSP "+event.getUser().getName()).queue();
                    break;
                case "shadowrun 5s":
                    //ttrpg Shadowrun, "Shadowrun 5th Edition Simplified"
                    event.reply("CG SR5S "+event.getUser().getName()).queue();
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
            //TODO: edit messages usable for creating threads for starting board game autoGM
            switch (event.getOption("game").getAsString().toLowerCase()) {
                case "chess":
                    //board game Leaving Earth
                    event.reply("Chess WIP").queue();
                    break;
                case "leaving earth":
                    //board game Leaving Earth
                    event.reply("Leaving Earth WIP").queue();
                    break;
                case "risk":
                    //board game Leaving Earth
                    event.reply("Risk WIP").queue();
                    break;
                default:
                    //in this case the board game is not supported
                    event.reply("Unrecognized Game: " + event.getOption("game").getAsString()
                            + "\n\nList of Supported Board Games:\n"
                            + "Chess(WIP)\n"
                            + "Leaving Earth(WIP)\n"
                            + "Risk(WIP)\n").queue();
                    break;
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }

    private void handleStartTTRPG(SlashCommandInteractionEvent event){
        try{
            //TODO: edit messages usable for creating threads for starting TTRPG autoGm
            switch (event.getOption("game").getAsString().toLowerCase()) {
                case "house games":
                    //ttrpg House Games
                    event.reply("HGRR AutoDM WIP").queue();
                    break;
                case "pathfinder 1e":
                    //ttrpg Pathfinder
                    event.reply("PF1E AutoDM WIP").queue();
                    break;
                case "pathfinder spheres":
                    //ttrpg Pathfinder
                    event.reply("PFSP AutoDM WIP").queue();
                    break;
                case "shadowrun 5s":
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
