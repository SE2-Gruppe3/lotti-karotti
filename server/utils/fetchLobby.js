/**
 * Fetches the lobby instance from the lobbies list
 * @param {*} lobbiesList 
 * @param {*} lobbyCode 
 * @returns lobby instance
 */
function fetchLobbyInstance(lobbiesList, lobbyCode){
    const lobby = lobbiesList.find(lobby => lobby.code === lobbyCode);
    return lobby;
}


module.exports= fetchLobbyInstance;