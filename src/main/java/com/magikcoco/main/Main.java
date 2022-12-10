package com.magikcoco.main;

import com.magikcoco.bot.Bot;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if(args.length == 1){
            try {
                Bot bot = Bot.createBotFromToken(args[0]);
                bot.addSlashCommands();
                bot.addListeners();
                Scanner scanner = new Scanner(System.in);
                //the loop exits when the program exits when exit is called
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
                System.exit(1);
            }
        } else {
            System.out.println("Needs one argument: "+'"'+"absolute/path/to/token/file"+'"');
        }
        //scanner for input from console
        //TODO: #8 wait until any console outputs are finished before displaying command prompt (needs #7 TODO first)
    }

    private static void handleExitCommand(){
        //exit handler
        System.exit(0);
    }

    private static void handlePrint(){
        System.out.println(Bot.getInstance().getBotName());
    }
}
