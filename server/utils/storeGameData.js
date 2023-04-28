function storeGameData(socket){

    var lobby = new Object();
    lobby.code = code;
    lobby.owner = owner;
    lobby.players = [];

    return lobby;
}
