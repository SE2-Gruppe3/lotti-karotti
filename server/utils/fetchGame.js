function fetchGameDataInstance(gameDataList, socketID){
    const game = gameDataList.find(game => game.sid === socketID);
    return game;
}

module.exports = fetchGameDataInstance;