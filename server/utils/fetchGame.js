/**
 * Fetches the game data instance from the gameDataList (ONLY ONE SINGLE GAME GETS RETURNED BASED ON THE SOCKET ID)
 * @param {*} gameDataList 
 * @param {*} socketID 
 * @returns game data instance
 */
function fetchGameDataInstance(gameDataList, socketID) {
    const game = gameDataList.find(game => game.sid === socketID);
    return game;
}

module.exports = fetchGameDataInstance;