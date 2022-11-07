package se.magello.micro;
import java.util.UUID;

/**
 *  The Database object.
 */
public class DBObject {
    public String id;
    public Integer revision;
    public String status;
    public String content;
    public Long creationTime;
    public String type;
    public String collection;
    static String STATUS_ALIVE = "STATUS_ALIVE";
    static String STATUS_REPLACED = "STATUS_REPLACED";
    static String STATUS_DELETED = "STATUS_DELETED";
    static String TYPE_NULLOBJECT = "__NULLOBJECT";
    static String TYPE_DBMETA = "__DBMETA";
    static String TYPE_DEFAULT = "__DBOBJ";
    
    public  DBObject(String content){
        this.id = UUID.randomUUID().toString();
        this.revision = 1;
        this.status = STATUS_ALIVE;
        this.creationTime = System.currentTimeMillis();
        this.content = content;
        this.collection = "";
        this.type = TYPE_DEFAULT;
    }

    public  DBObject(String collection, String content){
        this.id =  UUID.randomUUID().toString();
        this.revision = 1;
        this.status = STATUS_ALIVE;
        this.content = content;
        this.type = TYPE_DEFAULT;
        this.collection = collection;
    }

    public void setId(String newId){
        this.id = newId;
    }

    public void setRevision(Integer newRevision){
        this.revision = newRevision;
    }

    public void setType(String newType){
        this.type = newType;
    }

    public void setStatus(String newStatus){
        this.status = newStatus;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public static String escape(String s){
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("\'", "\\'")
                .replace("\"", "\\\"");
      }

    public String toJSONString(){
        // String jsonObj = "{}";
        // if (this.type != TYPE_NULLOBJECT){
        //     try{
        //         jsonObj = jsonMapper.writeValueAsString(this);
        //     }
        //     catch (Exception e){
        //         System.out.println(e.getStackTrace());
        //     }
        // }
        // return jsonObj;
    
        if (this.type == TYPE_NULLOBJECT){
            return "{}";
        }
        else {
            return "{ \"id\":\"" + this.id + "\",\"status\":\"" + this.status + "\",\"collection\":\"" + this.collection + "\",\"creationTime\":" + this.creationTime + ", \"type\":\"" + this.type + "\", \"revision\":" + this.revision + ", \"content\": \"" + escape(this.content) + "\"}";
        }
    }



}
