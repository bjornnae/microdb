
const HOSTNAME = "localhost" 
// Make shure that HOSTPORT reflects the microdb port.
const HOSTPORT = "8090"


function createResource(handlerFn, collectionName, content){
    const xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handlerFn);
    let urlstr = `http://${HOSTNAME}:${HOSTPORT}/${collectionName}/`
    console.log(urlstr)
    xhr.open("POST", urlstr);
    xhr.send(content);    
}



function reqListener () {
    console.log(this.responseText);
  }


function getResourceById(handlerFn, id){
    const oReq = new XMLHttpRequest();
    oReq.addEventListener("load", handlerFn);
    let urlstr = `http://${HOSTNAME}:${HOSTPORT}/${id}`
    console.log(urlstr)
    oReq.open("GET", urlstr);
    oReq.send();
}

function findCollections(){
    objLst = JSON.parse(this.responseText)
    console.debug(objLst)
    let coll = {}
    for (o in objLst){
        console.debug(`--> ${objLst[o].id}`)
        if (coll[objLst[o].collection] == undefined){
            coll[objLst[o].collection] = 1
        } else {coll[objLst[o].collection]++}
    }
    console.debug(coll)
}

function getAllResources(handlerFn){
      const xhr = new XMLHttpRequest();
      xhr.addEventListener("load", handlerFn);
      let urlstr = `http://${HOSTNAME}:${HOSTPORT}`
      console.log(urlstr)
      xhr.open("GET", urlstr);
      xhr.send();
}

function getCollection(handlerFn, collectionName){
    const oReq = new XMLHttpRequest();
    oReq.addEventListener("load", handlerFn);
    let urlstr = `http://${HOSTNAME}:${HOSTPORT}/${collectionName}/`
    console.log(urlstr)
    oReq.open("GET", urlstr);
    oReq.send();
}

function updateResource(handlerFn, id, revision, newContent){
    const xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handlerFn);
    let urlstr = `http://${HOSTNAME}:${HOSTPORT}/${id}/${revision}`
    console.log(urlstr)
    xhr.open("PUT", urlstr);
    xhr.send(newContent);  
}

function deleteResource(handlerFn, id, revision){
    const xhr = new XMLHttpRequest();
    xhr.addEventListener("load", handlerFn);
    let urlstr = `http://${HOSTNAME}:${HOSTPORT}/${id}/${revision}`
    console.log(urlstr)
    xhr.open("DELETE", urlstr);
    xhr.send();  
}





