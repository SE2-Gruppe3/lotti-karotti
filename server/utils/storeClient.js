/**
 * This function is used to store the client information in the object
 * @param {*} id 
 * @param {*} name 
 * @returns client object
 */
function storeClientInfo(id, name){

    var client = new Object();
    client.clientId = id;
    client.name = name;

    return client;
}

module.exports = storeClientInfo;