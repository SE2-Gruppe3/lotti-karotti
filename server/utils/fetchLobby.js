function fetchLobbyInstance(lobbiesList, lobbyCode){
    const lobby = lobbiesList.find(lobby => lobby.code === lobbyCode);
    return lobby;
}


module.exports= fetchLobbyInstance;