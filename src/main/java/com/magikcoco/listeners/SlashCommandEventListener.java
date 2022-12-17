package com.magikcoco.listeners;

import com.magikcoco.manager.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlashCommandEventListener extends ListenerAdapter {

    private static LoggingManager lm = LoggingManager.getInstance();
    private static DataManager dm = DataManager.getInstance();

    //options for character sheets
    private String[] chargenOptions = new String[]{"house games","pathfinder 1e","pathfinder spheres","shadowrun 5s"};
    //options for board game start
    private String[] startBgOptions = new String[]{"leaving earth", "risk", "chess"};
    //options for AutoDM
    private String[] startRPGOptions = new String[]{"house games","pathfinder 1e","pathfinder spheres","shadowrun 5s"};

    public SlashCommandEventListener(){
        //default constructor just logs that its been created
        lm.logInfo("SlashCommandEventListener has been created");
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("chargen") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(chargenOptions)
                    .filter(chargenOptions->chargenOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(chargenOptions ->new Command.Choice(chargenOptions, chargenOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        } else if(event.getName().equals("startbg") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(startBgOptions)
                    .filter(startBgOptions -> startBgOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(startBgOptions ->new Command.Choice(startBgOptions, startBgOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        } else if(event.getName().equals("startrpg") && event.getFocusedOption().getName().equals("game")){
            List<Command.Choice> options = Stream.of(startRPGOptions)
                    .filter(startRpgOptions -> startRpgOptions.startsWith(event.getFocusedOption().getValue()))
                    .map(startRpgOptions ->new Command.Choice(startRpgOptions, startRpgOptions))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        //listens for slashcommands
        switch(event.getName()){
            case "chargen":
                //chargen slash command, used to initiate character creation process
                handleChargen(event);
                break;
            case "help":
                //help slash command, for explaining purposes
                handleHelp(event);
                break;
            case "ping":
                //ping slash command, for testing purposes
                handlePing(event);
                break;
            case "rename":
                //rename slash command, for renaming threads
                handleRename(event);
                break;
            case "startbg":
                //startbg slash command, used to initiate a board game
                handleStartbg(event);
                break;
            case "startrpg":
                //startrpg slash command, used to initiate a ttrpg
                handleStartRPG(event);
                break;
            default:
                //default case is an unrecognized command
                break;
        }
    }

    /*
     * handler for chargen command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleChargen(SlashCommandInteractionEvent event){
        //all replies must be under 100 characters including name, which is always between 2 and 32 characters
        try {
            if(threadAllowedInChannel(event)){
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
            } else {
                event.reply("This command is not supported in this location").setEphemeral(true).queue();
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that command").setEphemeral(true).queue();
        }
    }

    /*
     * handler for help command, replies with list of commands
     */
    private void handleHelp(@NotNull SlashCommandInteractionEvent event){
        event.deferReply(true).queue(); //this response is an ephemeral message
        if(event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            for(ChargenManager manager : dm.getActiveChargenManagers()){
                if(event.getChannel().equals(manager.getChargenThread())){
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/ping - replies with pong\n"
                            +"other commands WIP\n").queue();
                    return;
                }
            }
            for(BoardGameManager manager : dm.getActiveBoardGameMangers()){
                if(event.getChannel().equals(manager.getGameThread())){
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/ping - replies with pong\n"
                            +"other commands WIP\n").queue();
                    return;
                }
            }
            for(RPGManager manager : dm.getActiveRPGMangers()){
                if(event.getChannel().equals(manager.getGameThread())){
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/ping - replies with pong\n"
                            +"other commands WIP\n").queue();
                    return;
                }
            }
        } else {
            event.getHook().sendMessage("Commands usable here:\n\n"
                    +"/ping - replies with pong\n"
                    +"/chargen [game] - starts a thread to create a character for the specified game\n"
                    +"/startbg [game] - starts a thread for playing a board game\n"
                    +"/startrpg [game] - starts a thread for playing a ttrpg with\n").queue();
        }
    }

    /*
     * handler for ping command, replies with pong
     */
    private void handlePing(@NotNull SlashCommandInteractionEvent event){
        //just a ping command
        event.reply("pong").queue();
    }

    private void handleRename(@NotNull SlashCommandInteractionEvent event){
        //rename a thread if you are in one, and ephemeral reply
        event.deferReply(true).queue();
        try{
            if(channelIsPublicThread(event)){
                event.getChannel().asThreadChannel().getManager().setName(
                        Objects.requireNonNull(event.getOption("name")).getAsString()
                ).queue();
                event.getHook().sendMessage("Renamed the thread to "
                        + Objects.requireNonNull(event.getOption("name")).getAsString()).queue();
            } else {
                event.getHook().sendMessage("This command is not supported in this location").queue();
            }
        } catch(NullPointerException e) {
            event.getHook().sendMessage("I didn't understand that command").queue();
        }
    }

    /*
     * handles startbg command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleStartbg(SlashCommandInteractionEvent event){
        try {
            if(threadAllowedInChannel(event)){
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
            } else {
                event.reply("This command is not supported in this location").setEphemeral(true).queue();
            }
        } catch(NullPointerException e){
            event.reply("I didn't understand that command").setEphemeral(true).queue();
        }
    }

    /*
     * handles startrpg command, replies with a code that gets used to create a thread by TextMessageListener
     */
    private void handleStartRPG(SlashCommandInteractionEvent event){
        try{
            if(threadAllowedInChannel(event)){
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
            } else {
                event.reply("This command is not supported in this location").setEphemeral(true).queue();
            }
        } catch (NullPointerException e){
            event.reply("I didn't understand that command").setEphemeral(true).queue();
        }
    }

    private boolean threadAllowedInChannel(@NotNull SlashCommandInteractionEvent event){
        return event.getChannelType().equals(ChannelType.TEXT)
                && !event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)
                && !event.getChannelType().equals(ChannelType.GUILD_PRIVATE_THREAD)
                && !event.getChannelType().equals(ChannelType.GUILD_NEWS_THREAD)
                && !event.getChannelType().equals(ChannelType.PRIVATE)
                && !event.getChannelType().equals(ChannelType.FORUM)
                && !event.getChannelType().equals(ChannelType.VOICE)
                && !event.getChannelType().equals(ChannelType.STAGE)
                && !event.getChannelType().equals(ChannelType.NEWS)
                && !event.getChannelType().equals(ChannelType.GROUP)
                && !event.getChannelType().equals(ChannelType.CATEGORY)
                && !event.getChannelType().equals(ChannelType.UNKNOWN);
    }

    private boolean channelIsPublicThread(@NotNull SlashCommandInteractionEvent event){
        return event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)
                && !event.getChannelType().equals(ChannelType.TEXT)
                && !event.getChannelType().equals(ChannelType.GUILD_PRIVATE_THREAD)
                && !event.getChannelType().equals(ChannelType.GUILD_NEWS_THREAD)
                && !event.getChannelType().equals(ChannelType.PRIVATE)
                && !event.getChannelType().equals(ChannelType.FORUM)
                && !event.getChannelType().equals(ChannelType.VOICE)
                && !event.getChannelType().equals(ChannelType.STAGE)
                && !event.getChannelType().equals(ChannelType.NEWS)
                && !event.getChannelType().equals(ChannelType.GROUP)
                && !event.getChannelType().equals(ChannelType.CATEGORY)
                && !event.getChannelType().equals(ChannelType.UNKNOWN);
    }
}
