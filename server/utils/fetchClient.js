/**
 * This function is used to fetch the client instance from the clients list
 * @param {*} clientsList 
 * @param {*} socketID 
 * @returns client instance
 */
function fetchClientInstance(clientsList,socketID){
    const client = clientsList.find(client => client.clientId === socketID);
    console.log(client.name);
    return client;
}


module.exports= fetchClientInstance;