package se.magello.micro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * File persistence to disk.
 * MicroDB is an inmemory database, but every change in the db is written to disk.
 * Currently support is lacking for reading from those dumps, so when a db is restarted it will have lost all memory.
 */
public class FilePersistenceImpl implements PersistenceAPI {

    static String DEFAULT_FILENAME =  "microdata";
    static Path DEFAULT_FILEPATH =  Paths.get( DEFAULT_FILENAME + ".json");

    @Override
    public void Serialize(MicroDB db) {
        String outp = db.getJSONAll();
        long tNow = System.currentTimeMillis();
        long tSoon = tNow + 1;

        Path nextVersionFilePath = Paths.get("", DEFAULT_FILENAME + tSoon + ".json" );
        Path currentVersionBackupFilePath = Paths.get("", DEFAULT_FILENAME + tNow + ".json" );
        byte[] buf = outp.getBytes();
        try {
            Files.write(nextVersionFilePath, buf);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //System.out.println("IN Catch 1");
            e.printStackTrace();
        }

        if (new File(DEFAULT_FILEPATH.toString()).exists()){
            try {
                Files.copy(DEFAULT_FILEPATH, currentVersionBackupFilePath);
            } catch (IOException e) {
                //System.out.println("IN Catch 2");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            File dbFile = new File(DEFAULT_FILEPATH.toString());
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            Files.move(nextVersionFilePath, DEFAULT_FILEPATH, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            //System.out.println("IN Catch 3");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    @Override
    public MicroDB DeSerialize(String fileName) {
        // TODO Auto-generated method stub

        return null;
    }

}
