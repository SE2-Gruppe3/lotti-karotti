function storeClientInfo(id, name){

    var client = new Object();
    client.clientId = id;
    client.name = name;

    return client;
}

module.exports = storeClientInfo;