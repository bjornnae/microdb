package se.magello.micro;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The database.
 * Has crud functionality.
 * Implemented as an Array List and not yet as some better storage and retrieval datatype.
 * Search is simply filtering over this Array list.
 */
public class MicroDB {
    public static Integer OBJECT_DELETED = 0;
    public static Integer BAD_REVISION = -1;
    public static Integer OBJECT_NOT_FOUND = -2;
    public long creationTime = System.currentTimeMillis();
    public int optime;
    public int uptime;
    public int in;
    public int out;
    public List<DBObject> obList = new ArrayList<DBObject>();
    private HashMap<String, Integer> dbIndex = new HashMap<String, Integer>(); 
    private HashMap<String, String[]> collectionIndex = new HashMap<String, String[]>();
    private PersistenceAPI persistenceEngine;

    public MicroDB(PersistenceAPI persistenceEng){
        this.persistenceEngine = persistenceEng;
        obList.add(new DBObject(DBObject.TYPE_DBMETA, "{\"creationtime\":" + Long.toString(this.creationTime) +", \"uptime\":0, \"optime\":0, \"in\":0, \"out\":0}"));
    }

    public void setPersistenceEngine(PersistenceAPI pe){
        this.persistenceEngine = pe;
    }

    public String create(String dbContent){
        DBObject e = new DBObject(dbContent);
        this.obList.add(e);
        this.dbIndex.put(e.id, this.obList.size()-1);
        persistenceEngine.Serialize(this);
        return e.id;
    }

    public String create(String collectionName, String dbContent){
        DBObject dbo = new DBObject(dbContent);
        String id = dbo.id;
        dbo.collection = collectionName;
        this.obList.add(dbo);
        this.dbIndex.put(dbo.id, this.obList.size()-1);
        this.persistenceEngine.Serialize(this);
        if (this.collectionIndex.containsKey(collectionName)){
            String[] collAry = collectionIndex.get(collectionName);
            collAry = Arrays.copyOf(collAry, collAry.length + 1);
            collAry[collAry.length - 1] = id;
            collectionIndex.put(collectionName, collAry );
        } else {
            String[] collAry = {id};
            collectionIndex.put(collectionName, collAry);
        }
        return id;
    }

    public DBObject getDBObjectById(String uuid){
        Integer index;
        index = this.dbIndex.get(uuid);
        if (index == null){
            return new DBObject(DBObject.TYPE_NULLOBJECT, "");
        }
        return this.obList.get(index);
    }

    public String getJSONObjById(String uuid){
        String result = "{}";
        DBObject d = getDBObjectById(uuid);
        if (d != null){
            result = d.toJSONString();
        } 
        return result;
    }

    public List<DBObject> getAll(){
        return this.obList;
    }

    public List<DBObject> getCollection(String collectionName){
        String[] ids = collectionIndex.get(collectionName);
        ArrayList<DBObject> collObjects = new ArrayList<DBObject>();
        for (String id : ids){
            collObjects.add(getDBObjectById(id));
        }
        return collObjects;
    }

    public String getJSONCollection(String collectionName){
        List<DBObject> objs = getCollection(collectionName);
        String collJSON = getJSONListFromObjects(objs);
        return collJSON;
    }

    private String getJSONListFromObjects(List<DBObject> objList){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (DBObject dbo : objList){
            sb.append(dbo.toJSONString());
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getJSONAll(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (DBObject dbo : this.obList){
            sb.append(dbo.toJSONString());
            i++;
            //TODO remove trailing comma from list.
            if (i < this.obList.size()){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public Integer update(String id, Integer revision, String newContent){
        DBObject dbo = getDBObjectById(id);
        if (dbo.type != DBObject.TYPE_NULLOBJECT){
            if (dbo.revision == revision) {
                // Append a new version
                DBObject dboNew = new DBObject(newContent);
                dboNew.content = newContent;
                dboNew.setCollection(dbo.collection);
                dboNew.setId(dbo.id);
                dboNew.setRevision(dbo.revision + 1);
                if (dbo.type != null) {dboNew.setType(dbo.type);}
                dbo.setStatus(DBObject.STATUS_REPLACED);
                this.obList.add(dboNew);
                this.dbIndex.put(dbo.id, this.obList.size()-1);
                this.persistenceEngine.Serialize(this);
                return dboNew.revision;    
                } 
            else return BAD_REVISION;     
        } 
        return OBJECT_NOT_FOUND;
    }

    public Integer delete(String id, Integer revision){
        DBObject dbo = getDBObjectById(id);
        if (dbo.type != DBObject.TYPE_NULLOBJECT){
            if (dbo.revision == revision) {
                // Append a new version with deleted status
                DBObject dboNew = new DBObject("");
                dboNew.setId(dbo.id);
                dboNew.setRevision(dbo.revision + 1);
                if (dbo.type != null) {dboNew.setType(dbo.type);}
                dbo.setStatus(DBObject.STATUS_REPLACED);
                dboNew.setStatus(DBObject.STATUS_DELETED);
                this.obList.add(dboNew);
                this.dbIndex.remove(dbo.id);
                return OBJECT_DELETED;    
                } 
            else return BAD_REVISION;     
        } 
        return OBJECT_NOT_FOUND;
    }

}
