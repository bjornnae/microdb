package se.magello.micro;

public interface PersistenceAPI {
    void Serialize(MicroDB db) ;
    MicroDB DeSerialize(String fileName);
}
