package com.magikcoco.listeners;

import com.magikcoco.manager.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
            case "endgame":
                handleEndGame(event);
                break;
            case "help":
                //help slash command, for explaining purposes
                handleHelp(event);
                break;
            case "joinasgm":
                handleJoinAsGM(event);
                break;
            case "joinasplayer":
                handleJoinAsPlayer(event);
                break;
            case "ping":
                //ping slash command, for testing purposes
                handlePing(event);
                break;
            case "quit":
                //ping slash command, for testing purposes
                handleQuit(event);
                break;
            case "rename":
                //rename slash command, for renaming threads
                handleRename(event);
                break;
            case "roll":
                //rename slash command, for renaming threads
                handleRoll(event);
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

    private void handleEndGame(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if(!event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            event.getHook().sendMessage("Command not supported in this location").queue();
            return;
        }
        try{
            for(ThreadManager threadManager : dm.getActiveThreadManagers()){
                if(event.getChannel().equals(threadManager.getThread())){
                    if(threadManager.getClass().equals(RPGThreadManager.class)){
                        if(Objects.equals(event.getMember(), ((RPGThreadManager) threadManager).getGameMaster())){
                            event.getHook().sendMessage("Game is over").queue();
                            threadManager.getThread().sendMessage("Game Over").queue();
                            threadManager.getThread().getManager().setLocked(true).queue();
                            dm.removeActiveManager(threadManager);
                        } else {
                            event.getHook().sendMessage("You need to be the GM to use that command here").queue();
                        }
                        return;
                    } else if(threadManager.getClass().equals(BoardGameThreadManager.class)){
                        for(Member player : threadManager.getPlayers()){
                            if(Objects.equals(event.getMember(), player)){
                                event.getHook().sendMessage("Game is over").queue();
                                threadManager.getThread().sendMessage("Game Over").queue();
                                threadManager.getThread().getManager().setLocked(true).queue();
                                dm.removeActiveManager(threadManager);
                                return;
                            }
                        } event.getHook().sendMessage("You need to be a player to end the game").queue();
                    } else {
                        event.getHook().sendMessage("Command not supported in this location").queue();
                        return;
                    }
                }
            }
        } catch(Exception e){
            event.getHook().sendMessage("I didn't understand that command").queue();
        }
    }

    /*
     * handler for help command, replies with list of commands
     */
    private void handleHelp(@NotNull SlashCommandInteractionEvent event){
        event.deferReply(true).queue(); //this response is an ephemeral message
        if(!event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            event.getHook().sendMessage("Commands usable here:\n\n"
                    +"/help - print the available commands\n"
                    +"/ping - replies with pong\n"
                    +"/chargen [game] - starts a thread to create a character for the specified game\n"
                    +"/startbg [game] - starts a thread for playing a board game\n"
                    +"/startrpg [game] - starts a thread for playing a ttrpg with\n").queue();
            return;
        }
        for(ThreadManager manager : dm.getActiveThreadManagers()){
            if(event.getChannel().equals(manager.getThread())){
                if(manager.getClass().equals(ChargenThreadManager.class)){
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/help - print the available commands\n"
                            +"/ping - replies with pong\n"
                            +"/rename - renames the thread to the given name\n"
                            +"other commands WIP\n").queue();
                    return;
                } else if(manager.getClass().equals(BoardGameThreadManager.class)) {
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/help - print the available commands\n"
                            +"/joinasplayer - join the current game in this thread as a player\n"
                            +"/ping - replies with pong\n"
                            +"/quit - removes you from the game\n"
                            +"/rename - renames the thread to the given name\n"
                            +"other commands WIP\n").queue();
                    return;
                } else if(manager.getClass().equals(RPGThreadManager.class)) {
                    event.getHook().sendMessage("Commands usable here:\n\n"
                            +"/help - print the available commands\n"
                            +"/joinasgm - join the current game in this thread as the gm\n"
                            +"/joinasplayer - join the current game in this thread as a player\n"
                            +"/ping - replies with pong\n"
                            +"/quit - removes you from the game\n"
                            +"/rename - renames the thread to the given name\n"
                            +"other commands WIP\n").queue();
                    return;
                }
            }
        }
    }

    /*
     * handler for the joinasgm command
     */
    private void handleJoinAsGM(@NotNull SlashCommandInteractionEvent event){
        //should only work in RPG threads
        event.deferReply(true).queue();
        if(!event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            event.getHook().sendMessage("Command not supported in this location").queue();
            return;
        }
        for(ThreadManager manager : dm.getActiveThreadManagers()){
            if(event.getChannel().equals(manager.getThread())){
                if(manager.getClass().equals(RPGThreadManager.class)){
                    //RPG thread
                    //safe cast, this can't happen unless the class is RPGThreadManager
                    if(((RPGThreadManager) manager).addGM(event.getMember())){
                        event.getChannel().asThreadChannel().addThreadMember(Objects.requireNonNull(event.getMember())).queue();
                        event.getHook().sendMessage("You were added to the game as the GM").queue();
                        lm.logInfo("Added "+event.getMember().getEffectiveName()+" to thread "+event.getChannel().getName());
                        return;
                    } else {
                        event.getHook().sendMessage("You were not added to the game as the GM").queue();
                        lm.logInfo("Did not add "+ Objects.requireNonNull(event.getMember()).getEffectiveName()+" to thread "+event.getChannel().getName());
                        return;
                    }
                } else {
                    //Any other thread
                    event.getHook().sendMessage("Command not supported in this location").queue();
                    return;
                }
            }
        }
        event.getHook().sendMessage("Command not supported in this location").queue();
    }

    /*
     * handler for the joinasplayer command
     */
    private void handleJoinAsPlayer(@NotNull SlashCommandInteractionEvent event){
        //should only work in board game and RPG threads
        event.deferReply(true).queue();
        if(!event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            event.getHook().sendMessage("Command not supported in this location").queue();
            return;
        }
        for(ThreadManager manager : dm.getActiveThreadManagers()){
            if(event.getChannel().equals(manager.getThread())){
                //if the current channel is equal to the managed thread
                if(manager.getClass().equals(BoardGameThreadManager.class)){
                    //board game thread
                    if(manager.addPlayer(event.getMember())){
                        event.getChannel().asThreadChannel().addThreadMember(Objects.requireNonNull(event.getMember())).queue();
                        event.getHook().sendMessage("You were added to the game as a player").queue();
                        lm.logInfo("Added "+event.getMember().getEffectiveName()+" to thread "+event.getChannel().getName());
                    } else {
                        event.getHook().sendMessage("You were not added to the game as a player").queue();
                        lm.logInfo("Did not add "+ Objects.requireNonNull(event.getMember()).getEffectiveName()+" to thread "+event.getChannel().getName());
                    }
                    return;
                } else if(manager.getClass().equals(RPGThreadManager.class)){
                    if(manager.addPlayer(event.getMember())){
                        event.getChannel().asThreadChannel().addThreadMember(Objects.requireNonNull(event.getMember())).queue();
                        event.getHook().sendMessage("You have been added to the game as a player").queue();
                        lm.logInfo("Added " + event.getMember().getEffectiveName() + " to thread " + event.getChannel().getName());
                    } else {
                        event.getHook().sendMessage("You were not added as a player").queue();
                        lm.logInfo("Did not add " + Objects.requireNonNull(event.getMember()).getEffectiveName() + " to thread " + event.getChannel().getName());
                    }
                    return;
                } else {
                    event.getHook().sendMessage("Command not supported in this location").queue();
                    return;
                }
            }
        }
        event.getHook().sendMessage("Command not supported in this location").queue();
    }

    /*
     * handler for ping command, replies with pong
     */
    private void handlePing(@NotNull SlashCommandInteractionEvent event){
        //just a ping command
        event.reply("pong").setEphemeral(true).queue();
    }

    private void handleQuit(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        if(!event.getChannelType().equals(ChannelType.GUILD_PUBLIC_THREAD)){
            event.getHook().sendMessage("Command not supported in this location").queue();
            return;
        }
        try {
            for (ThreadManager manager : dm.getActiveThreadManagers()) {
                if (event.getChannel().equals(manager.getThread())) {
                    if (manager.getClass().equals(BoardGameThreadManager.class)) {
                        //board game thread
                        if (manager.removePlayer(event.getMember())) {
                            event.getChannel().asThreadChannel().removeThreadMember(Objects.requireNonNull(event.getMember())).queue();
                            event.getHook().sendMessage("You were removed from this game").queue();
                            lm.logInfo("Removed " + event.getMember().getEffectiveName() + " from " + event.getChannel().getName());
                            return;
                        } else {
                            event.getHook().sendMessage("You were not removed from this game").queue();
                            lm.logInfo("Did not remove " + Objects.requireNonNull(event.getMember()).getEffectiveName() + " from " + event.getChannel().getName());
                            return;
                        }
                    } else if (manager.getClass().equals(RPGThreadManager.class)) {
                        //RPG thread
                        if (((RPGThreadManager) manager).removeGM(Objects.requireNonNull(event.getMember()))) {
                            event.getChannel().asThreadChannel().removeThreadMember(event.getMember()).queue();
                            event.getHook().sendMessage("You were removed from this game as GM").queue();
                            lm.logInfo("Removed " + event.getMember().getEffectiveName() + " from " + event.getChannel().getName());
                            return;
                        } else if (manager.removePlayer(event.getMember())) {
                            event.getChannel().asThreadChannel().removeThreadMember(event.getMember()).queue();
                            event.getHook().sendMessage("You were removed from this game as a player").queue();
                            lm.logInfo("Removed " + event.getMember().getEffectiveName() + " from " + event.getChannel().getName());
                            return;
                        } else {
                            event.getHook().sendMessage("You were not removed from this game").queue();
                            lm.logInfo("Did not remove " + event.getMember().getEffectiveName() + " from " + event.getChannel().getName());
                            return;
                        }
                    } else {
                        event.getHook().sendMessage("Command not supported in this location").queue();
                        return;
                    }
                }
            }
        } catch (Exception e){
            event.getHook().sendMessage("I didn't understand that").queue();
            lm.logError("There was an error handling quit:\n"+e.toString());
        }
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
        } catch(Exception e) {
            event.getHook().sendMessage("I didn't understand that command").queue();
        }
    }

    /*
     * roll some dice
     */
    private void handleRoll(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue(); //not ephemeral, response should be public
        try{
            int numDice = Objects.requireNonNull(event.getOption("dice")).getAsInt();
            int numSide = Objects.requireNonNull(event.getOption("side")).getAsInt();
            int[] rolls = new int[numDice];
            for(int i = 0; i < numDice; i++){
                //generate a random number from 1 to the number of sides (+1 for inclusive)
                rolls[i] = ThreadLocalRandom.current().nextInt(1,numSide+1);
            }
            int countAbove = event.getOption("count", 0, OptionMapping::getAsInt);
            if(event.getOption("sum", false, OptionMapping::getAsBoolean)){
                //reply with sum
                event.getHook().sendMessage("The sum of the roll is: "+IntStream.of(rolls).sum()).queue();
            } else if(event.getOption("mean", false, OptionMapping::getAsBoolean)) {
                //reply with mean
                event.getHook().sendMessage("The mean of the roll is: "+IntStream.of(rolls).average().toString().substring(14)).queue();
            } else if(event.getOption("median", false, OptionMapping::getAsBoolean)) {
                //reply with median
                Arrays.sort(rolls);
                if (rolls.length % 2 == 0) {
                    event.getHook().sendMessage("The median of the roll is: " + rolls[rolls.length/2] + " and " + rolls[(rolls.length/2)-1]).queue();
                } else {
                    event.getHook().sendMessage("The median of the roll is: " + rolls[rolls.length/2]).queue();
                }
            } else if(event.getOption("mode", false, OptionMapping::getAsBoolean)) {
                //reply with mode
                //counting sort since its unlikely that N will ever be large
                int[] count = new int[numSide+1];
                //noinspection ForLoopReplaceableByForEach
                for(int i = 0; i<rolls.length; i++){
                    count[rolls[i]]++;
                }
                int index = count.length-1;
                for(int i=count.length-2; i>-1; i--){
                    if(count[i] >= count[index]){
                        index = i;
                    }
                }
                event.getHook().sendMessage("The mode of the roll is: " + index).queue();
            } else if(event.getOption("range", false, OptionMapping::getAsBoolean)) {
                //reply with range
                int max = numDice*numSide;
                event.getHook().sendMessage("The possible range of the roll is: "+ numDice +"-"+max).queue();
            } else if(countAbove > 0) {
                //reply with a count of rolls with numbers above countAbove
                //counting sort since its unlikely that N will ever be large
                int[] count = new int[numSide+1];
                //noinspection ForLoopReplaceableByForEach
                for(int i = 0; i<rolls.length; i++){
                    if(rolls[i] > countAbove){
                        count[rolls[i]]++;
                    }
                }
                event.getHook().sendMessage(IntStream.of(count).sum()+" dice rolled above "+countAbove).queue();
            } else {
                //no special option was selected
                event.getHook().sendMessage("You rolled: " + Arrays.toString(rolls)).queue();
            }
            lm.logInfo("I rolled "+numDice+"d"+numSide+" in "+event.getChannel().getName());
        } catch(Exception e){
            event.getHook().sendMessage("Something went wrong, I didn't understand the command").queue();
            lm.logError("I failed to roll dice in "+event.getChannel().getName()+"\n"+e.toString());
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
                    default -> event.reply("Unrecognized game: "
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
                    default -> event.reply("Unrecognized game: "
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
