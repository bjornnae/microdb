package test;
import se.magello.micro.MicroDB;
import se.magello.micro.FilePersistenceImpl;


public class Test1 {
    public static void t1(){
        System.out.println("");
        System.out.println("// Create a new Database:");
        System.out.println("MicroDB db = new MicroDB();");
        MicroDB db = new MicroDB(new FilePersistenceImpl());

        System.out.println("");
        System.out.println("// Put some objects into the DB:");
        System.out.println("String id1 = db.create(\"Some content-1\");");
        String id1 = db.create("Some content-1");
        System.out.println("String id2 = db.create(\"Some content-2\");");
        String id2 = db.create("Some content-2");
        System.out.println("String id3 = db.create(\"Some content-3\");");
        String id3 = db.create("Some content-3");
        
        System.out.println("");
        System.out.println("// Find one object:");
        System.out.println("db.getJSONObjById(id1);");
        String a1 = db.getJSONObjById(id1);
        System.out.println("//> " + a1);

        System.out.println("");
        System.out.println("// Look for an object that does not exist:");
        System.out.println("db.getJSONObjById(\"Dummy.id.yes\");");
        String a2 = db.getJSONObjById("Dummy.id.yes");
        System.out.println("//> " + a2);

        System.out.println("");
        System.out.println("// Update an object:");
        System.out.println("db.getJSONObjById(id2);");
        String a3before = db.getJSONObjById(id2);
        System.out.println("//> " + a3before);
        System.out.println("db.getDBObjectById(id2).revision;");
        Integer currentRev = db.getDBObjectById(id2).revision;
        System.out.println("//> " + currentRev);
        System.out.println("db.update(id2, currentRev, \"Some content-2 updated\");");
        Integer res = db.update(id2, currentRev, "Some content-2 updated");
        System.out.println("//> " + res);
        System.out.println("db.getJSONObjById(id2);");
        String a3after = db.getJSONObjById(id2);
        System.out.println("//> " + a3after);

        System.out.println("");
        System.out.println("// Update an object out of sync:");
        System.out.println("db.getJSONObjById(id2);");
        String a4before = db.getJSONObjById(id2);
        System.out.println("//> " + a4before);
        System.out.println("currentRev2 = db.getDBObjectById(id2).revision;");
        Integer currentRev2 = db.getDBObjectById(id2).revision;
        System.out.println("//> " + currentRev2);
        System.out.println("db.update(id2, currentRev2 - 1, \"Some content-2 updated again.\");");
        Integer res4 = db.update(id2, currentRev2 - 1, "Some content-2 updated again.");
        System.out.println("//> " + res4);
        System.out.println("db.getJSONObjById(id2);");
        String a4after = db.getJSONObjById(id2);
        System.out.println("//> " + a4after);

        System.out.println("");
        System.out.println("// Delete an object:");
        System.out.println("db.getDBObjectById(id3).revision;");
        Integer currentRev3 = db.getDBObjectById(id3).revision;   
        System.out.println("//> " + currentRev3);
        System.out.println("db.delete(id3, currentRev3);");
        Integer resDelete = db.delete(id3, currentRev3);
        System.out.println("//> " + resDelete);
        System.out.println("db.getJSONObjById(id3);");
        String a4 = db.getJSONObjById(id3);
        System.out.println("//> " + a4);


        System.out.println("");
        System.out.println("// Get all objects:");
        System.out.println("db.getJSONAll();");
        String all = db.getJSONAll();
        System.out.println("//> " + all);
    }    

    public static void main(){
        t1();
    }
}
