/**
 * Fetches all the games in a lobby
 * @param {*} gameDataList 
 * @param {*} socketID 
 * @returns game data
 */
function fetchLobbyGameData(gameDataList, lobbycode) {
    const lobbygame = gameDataList.filter(game => game.lobbycode === lobbycode);
    return lobbygame;
}

module.exports = fetchLobbyGameData;