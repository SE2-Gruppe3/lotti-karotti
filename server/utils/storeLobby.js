function storeLobbyInfo(code, owner){

    var lobby = new Object();
    lobby.code = code;
    lobby.owner = owner;
    lobby.players = [];

    return lobby;
}

module.exports = storeLobbyInfo;