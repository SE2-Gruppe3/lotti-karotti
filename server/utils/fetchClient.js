function fetchClientInstance(clientsList,socketID){
    const client = clientsList.find(client => client.clientId === socketID);
    console.log(client.name);
    return client;
}


module.exports= fetchClientInstance;