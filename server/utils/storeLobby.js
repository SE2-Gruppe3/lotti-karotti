/**
 * Stores the lobby information in an object
 * @param {*} code 
 * @param {*} owner 
 * @param {*} hole
 * @param {*} mutator
* @returns lobby object
 */
function storeLobbyInfo(code, owner, hole, mutator){

    var lobby = new Object();
    lobby.code = code;
    lobby.owner = owner;
    lobby.players = [];
    lobby.socket_turn = 0;
    lobby.hole = hole;
    lobby.holeTwo = -1;
    lobby.mutator = mutator;
    lobby.game_started = 0;

    return lobby;
}

module.exports = storeLobbyInfo;