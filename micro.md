# Micro framework

The micro framework is a persistence component that can be used for small size stuff (e.g. text editors, web applications ...). It is inspired by CoachDB but is super small and only depends on a Java JVM to run.

Data is kept in memory for as long as the server is running, but can be persisted in a text file (micro.db)

1. Start the micro.jar. It will expose a http server on port 8087 (or other as defined in MicroServer.java ).
2. Write code that calls  POST http://localhost:8087 and publishes JSON objects in the http body.
3. Write code that calls GET http://localhost:8087 to get all the published JSON objects from the micro db. Or, call GET http://localhost:8087/123458903 to get a specific object with _id = 123458903.
4. Write code that calls PUT http://localhost:8087/<id>/<revision> with an object in the body that is meant to be updated.
5. When you are done with your object, delete it by calling DELETE http://localhost:8087/<id>/<revision>


## Collections

Micro intercepts all calls to the root (i.e. http://localhost:<port>/)

Messages can be partitioned into collections by adding a collection name after the root like this:

    http://localhost:<port>/collectionName/

(trailing dash is mandatory)

All messages adhering to a specific collection can be retrived by adding the collection name to the get request:

    Get http://localhost:<port>/collectionName/

Note that all messages, regardless collection home, will still be listed in a HTTP-Get call to / .

Collection urls can be used to mock microservices. 


## JAVA API

    MicroDB JAVA API Usage
    
    // Create a new Database:
    MicroDB db = new MicroDB();
    
    // Put some objects into the DB:
    String id1 = db.create("Some content-1");
    String id2 = db.create("Some content-2");
    String id3 = db.create("Some content-3");
    
    // Find one object:
    db.getJSONObjById(id1);
    //> { "_id":"1a7e8d8e-5750-4deb-8cb7-5398ae722795","_creationTime":1648735249627, "_rev":1, "content": "Some content-1"}
    
    // Look for an object that does not exist:
    db.getJSONObjById("Dummy.id.yes");
    //> {}
    
    // Update an object:
    db.getJSONObjById(id2);
    //> { "_id":"c997a2d6-a7a6-4d1a-828d-b941db3536dd","_creationTime":1648735249628, "_rev":1, "content": "Some content-2"}
    db.getDBObjectById(id2).revision;
    //> 1
    db.update(id2, currentRev, "Some content-2 updated");
    //> 2
    db.getJSONObjById(id2);
    //> { "_id":"c997a2d6-a7a6-4d1a-828d-b941db3536dd","_creationTime":1648735249637, "_rev":2, "content": "Some content-2 updated"}
    
    // Delete an object:
    db.getDBObjectById(id3).revision;
    //> 1
    db.delete(id3, currentRev2);
    //> 0
    db.getJSONObjById(id3);
    //> {}
    
    // Get all objects:
    db.getJSONAll();
    //> [{ "_id":"de2c1ccf-4cc2-4045-ba77-64b4e52824d7","_creationTime":null, "_type":"__DBMETA", "_rev":1, "content": "{\"creationtime\":1648735248538, \"uptime\":0, \"optime\":0, \"in\":0, \"out\":0"},{ "_id":"1a7e8d8e-5750-4deb-8cb7-5398ae722795","_creationTime":1648735249627, "_rev":1, "content": "Some content-1"},{ "_id":"c997a2d6-a7a6-4d1a-828d-b941db3536dd","_creationTime":1648735249628, "_rev":1, "content": "Some content-2"},{ "_id":"e2f83912-44a7-4082-991c-213ce4f9a72e","_creationTime":1648735249628, "_rev":1, "content": "Some content-3"},{ 
    "_id":"c997a2d6-a7a6-4d1a-828d-b941db3536dd","_creationTime":1648735249637, "_rev":2, "content": "Some content-2 updated"},{ "_id":"e2f83912-44a7-4082-991c-213ce4f9a72e","_creationTime":1648735249640, "_rev":2, "content": ""},]

##Build 
* Open a command line and go to the root of the project (/microdb/)
* Type "gradle clean customFatjar" the project is built and resulting artifacts can be found under the folder microdb/build/lib

#Run the application
* Open a command line and go to the build folder (mentionend under build above).
* Type "java -jar micro.jar" The application should now run as long as the terminal is open.
* To stop the application press "cmd + c" or close the terminal window. 



##TODO

    * See if xml can be stored as some kind of payload.
    * Make configurable by property file (add ConfigurationParser class maybe).
    * Rebuild collection index from db-file at restart. Currently collection index is only kept in memory and lost at restart.
