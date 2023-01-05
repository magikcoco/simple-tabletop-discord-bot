package com.magikcoco.manager;

import com.magikcoco.manager.thread.ThreadManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

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

    public void updatePlayersInThreadDocument(String threadID, @NotNull Member[] players){
        List<String> playerIDs = new ArrayList<>();
        for(Member member : players){
            if(member != null){
                playerIDs.add(member.getId());
            }
        }
        managedThreads.updateOne(Filters.eq("thread-id",threadID), Updates.set("players", playerIDs));
    }

    public void updateGMInThreadDocument(String threadID, Member gm){
        if(gm != null){
            managedThreads.updateOne(Filters.eq("thread-id",threadID), Updates.set("gm", gm.getId()));
        } else {
            managedThreads.updateOne(Filters.eq("thread-id",threadID), Updates.set("gm", null));
        }
    }

    public List<ThreadManager> getActiveThreadManagers(){
        return activeThreadManagers;
    }

    public static DataManager getInstance(){
        return INSTANCE;
    }

    //TODO: load data from database on restart
}
