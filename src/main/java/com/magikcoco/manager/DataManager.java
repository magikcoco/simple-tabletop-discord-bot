package com.magikcoco.manager;

import com.magikcoco.bot.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private Bot bot = Bot.getInstance();
    private List<ThreadManager> activeThreadManagers;
    private Properties properties;
    private LoggingManager lm = LoggingManager.getInstance();
    private static final String[] RPGLIST = new String[]{"HGRR","PF1E","PFSP","SR5S"};
    private static final String[] BOARDGAMELIST = new String[]{"CHSS","LEEA","RISK"};

    private DataManager(){
        //default constructor
        activeThreadManagers = new ArrayList<>();
        properties = new Properties();
        try {
            Properties loadProperties = new Properties();
            File propertiesFile = new File("simple-tabletop-bot.properties");
            if(propertiesFile.exists()){
                FileInputStream in = new FileInputStream(propertiesFile);
                loadProperties.load(in);
                in.close();
                loadDataFromPropertiesFile(loadProperties);
            } else {
                //noinspection ResultOfMethodCallIgnored
                propertiesFile.createNewFile();
            }
        } catch (Exception e) {
            lm.logError("Critical error loading properties from file");
            System.exit(3);
        }
    }

    public static DataManager getInstance(){
        //get the singleton instance
        return INSTANCE;
    }

    public void addActiveManager(@NotNull ThreadManager manager){
        try {
            FileOutputStream out = new FileOutputStream("simple-tabletop-bot.properties");
            properties.setProperty(manager.getThread().getId(),manager.getGameCode());
            properties.store(out, "---added manager---");
            out.close();
        } catch (FileNotFoundException e) {
            lm.logError("Couldn't store an active manager that was added to the DataManager in properties file");
        } catch (IOException e) {
            lm.logError("IOException storing new manager in properties file");
        } finally {
            activeThreadManagers.add(manager);
        }
    }

    public void removeActiveManager(@NotNull ThreadManager manager){
        try {
            FileOutputStream out = new FileOutputStream("simple-tabletop-bot.properties");
            properties.remove(manager.getThread().getId());
            properties.store(out, "---removed manager---");
            out.close();
        } catch (FileNotFoundException e) {
            lm.logError("Couldn't store an active manager that was added to the DataManager in properties file");
        } catch (IOException e) {
            lm.logError("IOException storing new manager in properties file");
        } finally {
            activeThreadManagers.remove(manager);
        }
    }

    public List<ThreadManager> getActiveThreadManagers(){
        return activeThreadManagers;
    }

    public Properties getProperties(){
        return properties;
    }

    private void loadDataFromPropertiesFile(@NotNull Properties loadProperties){
        for(Object object : loadProperties.keySet()){
            if(object.getClass().equals(String.class)){
                String key = (String)object;
                String value = loadProperties.getProperty(key);
                if(Arrays.asList(RPGLIST).contains(value)){
                    ThreadChannel threadChannel = bot.getThreadByID(key);
                    RPGThreadManager manager = new RPGThreadManager(threadChannel);
                    addActiveManager(manager);
                    for(ThreadMember member : threadChannel.retrieveThreadMembers().complete()){
                        if(!manager.addPlayer(member.getMember())){
                            break;
                        }
                    }
                } else if(Arrays.asList(BOARDGAMELIST).contains(value)) {
                    ThreadChannel threadChannel = bot.getThreadByID(key);
                    BoardGameThreadManager manager = new BoardGameThreadManager(threadChannel);
                    addActiveManager(manager);
                    for(ThreadMember member : threadChannel.retrieveThreadMembers().complete()){
                        if(!manager.addPlayer(member.getMember())){
                            break;
                        }
                    }
                } else {
                    ThreadChannel threadChannel = bot.getThreadByID(key);
                    User user = bot.getUserByID(value);
                    if(user != null){
                        Member member = threadChannel.retrieveThreadMember(user).complete().getMember();
                        RPGThreadManager manager = new RPGThreadManager(threadChannel);
                        addActiveManager(manager);
                        manager.addGM(member);
                        for(ThreadMember threadMember : threadChannel.retrieveThreadMembers().complete()){
                            if(!threadMember.getMember().equals(member)){
                                if(!manager.addPlayer(threadMember.getMember())){
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //TODO: Test Permanency
}
