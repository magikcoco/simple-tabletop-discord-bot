package com.magikcoco.main;

import com.magikcoco.bot.Bot;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //bot doing the work
        Bot bot = Bot.getInstance();
        //console commands
        while(true){
            //infinite loop
            System.out.print("Type 'exit' to exit: ");
            String input = scanner.nextLine();
            if(input.toLowerCase().trim().equals("exit")){
                System.exit(0);
            } else {
                System.out.println("unrecognized command");
            }
            System.out.println();
        }
    }

}
