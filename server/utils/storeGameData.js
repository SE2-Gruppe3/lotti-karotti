/**
 * This is the single object for every player for the game datastructure that will be stored on the gerver and sent to the clients.
 * @param {*} socketid 
 * @param {*} lobbycode 
 * @returns GameData Object
 */
function storeGameData(socketid, lobbycode, color) {
    var playerData = {
      id: socketid,
      lobbycode: lobbycode,
      color: color,
      rabbits: [
        { name: 'rabbit1', position: 0 },
        { name: 'rabbit2', position: 0 },
        { name: 'rabbit3', position: 0 },
        { name: 'rabbit4', position: 0 },
      ],
    };
  
    return playerData;
  }
  
