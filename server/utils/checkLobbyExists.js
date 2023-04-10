function lobbyExists(lobbiesList, lobbyCode){
    const lobby = lobbiesList.find(lobby => lobby.code === lobbyCode);
    return lobby ? 1 : 0;
}

module.exports = lobbyExists;