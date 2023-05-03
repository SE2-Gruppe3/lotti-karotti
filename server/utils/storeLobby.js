/**
 * Stores the lobby information in an object
 * @param {*} code 
 * @param {*} owner 
 * @returns lobby object
 */
function storeLobbyInfo(code, owner){

    var lobby = new Object();
    lobby.code = code;
    lobby.owner = owner;
    lobby.players = [];

    return lobby;
}

module.exports = storeLobbyInfo;