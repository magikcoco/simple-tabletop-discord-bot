package com.magikcoco.manager;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private List<ThreadManager> activeThreadManagers;

    private DataManager(){
        //default constructor
        activeThreadManagers = new ArrayList<>();
    }

    public static DataManager getInstance(){
        //get the singleton instance
        return INSTANCE;
    }

    public void addActiveManager(ThreadManager manager){
        activeThreadManagers.add(manager);
    }

    public void removeActiveManager(ThreadManager manager){
        activeThreadManagers.remove(manager);
    }

    public List<ThreadManager> getActiveThreadManagers(){
        return activeThreadManagers;
    }

    //TODO: permanency for data manager
}
