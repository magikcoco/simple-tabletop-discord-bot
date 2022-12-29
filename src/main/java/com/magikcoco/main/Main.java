package com.magikcoco.main;

import com.magikcoco.bot.Bot;
import com.magikcoco.manager.DataManager;
import com.magikcoco.manager.LoggingManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static LoggingManager lm = LoggingManager.getInstance();
    private static DataManager dm = DataManager.getInstance();

    public static void main(String[] args) {
        lm.logInfo("Application Start");
        if(args.length == 1){
            try {
                Bot bot = Bot.createBotFromToken(args[0]);
                bot.addSlashCommands();
                bot.addListeners();
                Scanner scanner = new Scanner(System.in);
                //the loop exits when the program exits when exit is called
                lm.logInfo("Bot is created, waiting for command now");
                //noinspection InfiniteLoopStatement
                while (true) {
                    //give prompt for console commands
                    System.out.print("\nType a command: ");
                    String input = scanner.nextLine();
                    switch (input.toLowerCase().trim()) {
                        //exit command exits program
                        case "exit" -> handleExitCommand();
                        case "print name" -> handlePrint();
                        //the command was not recognized
                        default -> {
                            System.out.println("Unrecognized command. Commands are:");
                            System.out.println("exit - exit the program");
                            System.out.println("print name - prints the name of this bot");
                        }
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                lm.logError("IOException in main method:\n"+e.toString());
                System.exit(1);
            }
        } else {
            lm.logInfo("Invalid number of arguments. Should have only 1 argument.");
        }
        //TODO: #8 wait until any console outputs are finished before displaying command prompt (needs #7 TODO first)
    }

    private static void handleExitCommand(){
        //exit handler
        lm.logInfo("Exit command called, shutting down");
        try {
            dm.getProperties().store(new FileOutputStream("simple-tabletop-bot.properties"), "---no comment---");
        } catch (Exception e) {
            lm.logError("Couldn't save properties on exit:\n"+e.toString());
        }
        System.exit(0);
    }

    private static void handlePrint(){
        lm.logInfo("Printed bot name to console. Bot name is: "+Bot.getInstance().getBotName());
        System.out.println(Bot.getInstance().getBotName());
    }
}
