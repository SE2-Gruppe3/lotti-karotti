/**
 * Stores the lobby information in an object
 * @param {*} code 
 * @param {*} owner 
 * @param {*} hole
 * @returns lobby object
 */
function storeLobbyInfo(code, owner, hole){

    var lobby = new Object();
    lobby.code = code;
    lobby.owner = owner;
    lobby.players = [];
    lobby.socket_turn = 0;
    lobby.hole = hole;
    lobby.game_started = 0;

    return lobby;
}

module.exports = storeLobbyInfo;