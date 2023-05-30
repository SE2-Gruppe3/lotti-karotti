/**
 * Update gameData with new positions if either player is overlapping or player is winning or something else that will get implemented soon
 * @param {*} gameData 
 * @returns gamedata with updated positions 
 */
function positionOverlap(gameData, pos) {
    if (gameData) {
        for (var i = 0; i < gameData.length; i++) {
            for (var j = 0; j < 4; j++) {
                if (gameData[i].rabbits[j].position === pos) {
                    console.log("[Server] Rabbit " + j + " of player " + i + " is getting kicked back to start!")
                    gameData[i].rabbits[j].position = 0;
                    break;
                }
            }
        }
    }
    return gameData;
}

module.exports = positionOverlap;