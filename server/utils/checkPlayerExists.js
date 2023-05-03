/**
 * Check if player exists in the clients list
 * @param {*} clientsList 
 * @param {*} socketID 
 * @returns 1 if player exists, otherwise 0
 */
function playerExists(clientsList, socketID){
    const client = clientsList.find(client => client.clientId === socketID);
    return client ? 1 : 0;
}


module.exports= playerExists;