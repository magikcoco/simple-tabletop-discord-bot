package com.magikcoco.main;

import com.magikcoco.bot.Bot;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        //bot doing the work
        Bot bot = Bot.getInstance();
        //scanner for input from console
        Scanner scanner = new Scanner(System.in);
        while(true){
            //give prompt for console commands
            System.out.print("Type a command: ");
            String input = scanner.nextLine();
            switch(input.toLowerCase().trim()){
                case "exit":
                    //exit command exits program
                    handleExitCommand();
                    break;
                default:
                    //the command was not recognized
                    System.out.println("Unrecognized command. Commands are:");
                    System.out.println("exit - exit the program");
                    break;
            }
            System.out.println();
        }
    }

    private static void handleExitCommand(){
        //exit handler
        System.exit(0);
    }

}
