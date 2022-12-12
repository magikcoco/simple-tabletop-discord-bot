package com.magikcoco.manager;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private List<ChargenManager> activeChargenManagers;

    private DataManager(){
        //default constructor
        activeChargenManagers = new ArrayList<>();
    }

    public static DataManager getInstance(){
        //get the singleton instance
        return INSTANCE;
    }

    public void addActiveManager(ChargenManager manager){
        activeChargenManagers.add(manager);
    }

    public void removeActiveManager(ChargenManager manager){
        activeChargenManagers.remove(manager);
    }

    public List<ChargenManager> getActiveChargenManagers(){
        return activeChargenManagers;
    }
}
