package com.magikcoco.manager;

import com.magikcoco.manager.thread.ThreadManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final DataManager INSTANCE = new DataManager();
    private List<ThreadManager> activeThreadManagers;
    private MongoClient mongoClient; //the connection to the mongo server
    private MongoDatabase mongoDatabase; //the database for storing information
    private MongoCollection<Document> managedThreads; //the collection for managed threads

    private DataManager(){
        //default constructor
        activeThreadManagers = new ArrayList<>();
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017")); //default config for mongoserver
        mongoDatabase = mongoClient.getDatabase("simple-tabletop-bot-data");
        managedThreads = mongoDatabase.getCollection("managed-threads");
    }

    public void addActiveManager(ThreadManager manager){
        activeThreadManagers.add(manager);
        Document managedThread = new Document()
                .append("thread-id",manager.getThread().getId())
                .append("manager-type",manager.getClass().getName())
                .append("players",null)
                .append("gm",null);
        managedThreads.insertOne(managedThread);
    }

    public void removeActiveManager(ThreadManager manager){
        activeThreadManagers.remove(manager);
        managedThreads.deleteOne(Filters.eq("thread-id",manager.getThread().getId()));
    }

    public List<ThreadManager> getActiveThreadManagers(){
        return activeThreadManagers;
    }

    public static DataManager getInstance(){
        return INSTANCE;
    }

    //TODO: convert to using a database to avoid losing data on restart
}
