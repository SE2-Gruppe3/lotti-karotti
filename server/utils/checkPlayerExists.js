function playerExists(clientsList, socketID){
    const client = clientsList.find(client => client.clientId === socketID);
    return client ? 1 : 0;
}


module.exports= playerExists;