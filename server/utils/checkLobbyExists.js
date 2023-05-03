/**
 * Checks if a lobby exists in the lobbies list
 * @param {*} lobbiesList 
 * @param {*} lobbyCode 
 * @returns 1 if lobby exists, otherwise 0
 */
function lobbyExists(lobbiesList, lobbyCode){
    const lobby = lobbiesList.find(lobby => lobby.code === lobbyCode);
    return lobby ? 1 : 0;
}

module.exports = lobbyExists;