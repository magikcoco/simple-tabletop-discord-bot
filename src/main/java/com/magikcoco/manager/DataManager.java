package com.magikcoco.manager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private List<ThreadManager> activeThreadManagers;
    private MongoClient mongoClient; //the connection to the mongo server
    private MongoDatabase mongoDatabase; //the database for storing information

    private DataManager(){
        //default constructor
        activeThreadManagers = new ArrayList<>();
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017")); //default config for mongoserver
        mongoDatabase = mongoClient.getDatabase("simple-tabletop-bot-data");
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

    //TODO: convert to using a database to avoid losing data on restart
}
