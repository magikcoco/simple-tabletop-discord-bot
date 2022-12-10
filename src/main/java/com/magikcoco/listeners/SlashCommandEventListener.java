package com.magikcoco.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandEventListener extends ListenerAdapter {
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
                handleHelp(event);
                break;
            case "ping":
                //ping slash command, for testing purposes
                handlePing(event);
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

    /*
     * handler for help command, replies with list of commands
     */
    private void handleHelp(SlashCommandInteractionEvent event){
        event.reply("/ping - replies with pong\n"
                +"/charcreate [game] - starts a thread to create a character for the specified game\n"
                +"/startboardgame [game] - starts a thread for playing a board game\n"
                +"/startttrpg [game] - starts a thread for playing a ttrpg with\n").queue();
    }

    /*
     * handler for ping command, replies with pong
     */
    private void handlePing(SlashCommandInteractionEvent event){
        event.reply("pong").queue();
    }

    /*
     * handler for charcreate command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleCharCreate(SlashCommandInteractionEvent event){
        //all replies must be under 100 characters including name, which is always between 2 and 32 characters
        try {
            switch (Objects.requireNonNull(event.getOption("game")).getAsString().toLowerCase()) {
                //ttrpg House Games, "House Games Revised Rules"
                case "house games" -> event.reply("CG HGRR " + event.getUser().getName()).queue();
                //ttrpg Pathfinder, "Pathfinder First Edition"
                case "pathfinder 1e" -> event.reply("CG PF1E " + event.getUser().getName()).queue();
                //ttrpg Pathfinder, "Pathfinder Spheres of Power/Might"
                case "pathfinder spheres" -> event.reply("CG PFSP " + event.getUser().getName()).queue();
                //ttrpg Shadowrun, "Shadowrun 5th Edition Simplified"
                case "shadowrun 5s" -> event.reply("CG SR5S " + event.getUser().getName()).queue();
                //in this case the ttrpg is unsupported
                default -> event.reply("Unrecognized Character Sheet: "
                        + Objects.requireNonNull(event.getOption("game")).getAsString()
                        + "\n\nList of Supported Character Sheets:\n"
                        + "House Games(WIP)\n"
                        + "Pathfinder 1e(WIP)\n"
                        + "Pathfinder Spheres(WIP)\n"
                        + "Shadowrun 5S(WIP)\n")
                        .queue();
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }

    /*
     * handles startboardgame command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleStartBoardGame(SlashCommandInteractionEvent event){
        try {
            switch (Objects.requireNonNull(event.getOption("game")).getAsString().toLowerCase()) {
                //board game Chess
                case "chess" -> event.reply("BG CHSS 2-2").queue();
                //board game Leaving Earth
                case "leaving earth" -> event.reply("BG LEEA 1-5").queue();
                //board game Risk
                case "risk" -> event.reply("BG RISK 3-6").queue();
                //in this case the board game is not supported
                default -> event.reply("Unrecognized Game: "
                        + Objects.requireNonNull(event.getOption("game")).getAsString()
                        + "\n\nList of Supported Board Games:\n"
                        + "Chess(WIP)\n"
                        + "Leaving Earth(WIP)\n"
                        + "Risk(WIP)\n").queue();
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }

    /*
     * handles startttrpg command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleStartTTRPG(SlashCommandInteractionEvent event){
        try{
            switch (Objects.requireNonNull(event.getOption("game")).getAsString().toLowerCase()) {
                //ttrpg House Games
                case "house games" -> event.reply("RG HGRR").queue();
                //ttrpg Pathfinder
                case "pathfinder 1e" -> event.reply("RG PF1E").queue();
                //ttrpg Pathfinder
                case "pathfinder spheres" -> event.reply("RG PFSP").queue();
                //ttrpg Shadowrun
                case "shadowrun 5s" -> event.reply("RG SR5S").queue();
                //in this case the game is unsupported
                default -> event.reply("Unrecognized Game: "
                        + Objects.requireNonNull(event.getOption("game")).getAsString()
                        + "\n\nList of Supported Games:\n"
                        + "House Games(WIP)\n"
                        + "Pathfinder 1e(WIP)\n"
                        + "Pathfinder Spheres(WIP)\n"
                        + "Shadowrun 5S(WIP)\n")
                        .queue();
            }
        } catch (NullPointerException e){
            event.reply("I didn't understand that").queue();
        }
    }
}
