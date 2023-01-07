package com.magikcoco.manager;


import com.magikcoco.manager.thread.ChargenThreadManager;
import com.magikcoco.manager.thread.ThreadManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.Member;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
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
        if(!getDataFromDatabase()){
            System.exit(9001);
        }
    }

    public void addActiveManager(ThreadManager manager){
        activeThreadManagers.add(manager);
        Document managedThread = new Document()
                .append("thread-id",manager.getThread().getId())
                .append("manager-type",manager.getClass().getName())
                .append("players",null)
                .append("gm",null);
        managedThreads.insertOne(managedThread);
        if(manager.getClass().equals(ChargenThreadManager.class)){
            manager.addPlayer(((ChargenThreadManager) manager).getPlayer());
        }
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

    private boolean getDataFromDatabase(){
        System.out.println("Getting from database");
        FindIterable<Document> iterableDocuments = managedThreads.find();
        for (Document document : iterableDocuments) {
            System.out.println("data exists");
            String threadID = (String) document.get("thread-id"); //this should always be a string
            String managerType = (String) document.get("manager-type"); //this should also always be a string
            //this is always going to be a list of strings
            @SuppressWarnings("unchecked") ArrayList<String> playerIDs = (ArrayList<String>) document.get("players");
            String gmID = (String) document.get("gm");
            //TODO: load data from database on restart
        }
        return true;
    }
}
