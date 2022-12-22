package com.magikcoco.manager;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private List<Manager> activeManagers;
    private List<ChargenManager> activeChargenManagers;
    private List<BoardGameManager> activeBoardGameMangers;
    private List<RPGManager> activeRPGMangers;

    private DataManager(){
        //default constructor
        activeManagers = new ArrayList<>();
        activeChargenManagers = new ArrayList<>();
        activeBoardGameMangers = new ArrayList<>();
        activeRPGMangers = new ArrayList<>();
    }

    public static DataManager getInstance(){
        //get the singleton instance
        return INSTANCE;
    }

    public void addActiveManager(ChargenManager manager){
        activeChargenManagers.add(manager);
    }

    public void addActiveManager(BoardGameManager manager){
        activeBoardGameMangers.add(manager);
    }

    public void addActiveManager(RPGManager manager){
        activeRPGMangers.add(manager);
    }

    public void removeActiveManager(ChargenManager manager){
        activeChargenManagers.remove(manager);
    }

    public void removeActiveManager(BoardGameManager manager){
        activeBoardGameMangers.remove(manager);
    }

    public void removeActiveManager(RPGManager manager){
        activeRPGMangers.remove(manager);
    }

    public List<Manager> getActiveManagers(){
        return activeManagers;
    }

    public List<ChargenManager> getActiveChargenManagers(){
        return activeChargenManagers;
    }

    public List<BoardGameManager> getActiveBoardGameMangers(){
        return activeBoardGameMangers;
    }

    public List<RPGManager> getActiveRPGMangers(){
        return activeRPGMangers;
    }

    //TODO: permanency for data manager
}
