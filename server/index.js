/**
 * @file index.js is the main file of the server. It handles all the connections and the logic behind the game.
 * @version 1.0.0
 * @module index
 * 
 * @requires express
 * @requires socket.io
 *  
 */

const server = require('./utils/server.js');
const settings = require('./utils/settings.js');
const socket = require('./utils/socket.js');
const storeClientInfo = require('./utils/storeClient.js');
const storeLobbyInfo = require('./utils/storeLobby.js');
const playerExist = require('./utils/checkPlayerExists.js');
const lobbyExist = require('./utils/checkLobbyExists.js');
const fetchClientInstance = require('./utils/fetchClient.js');
const fetchLobbyInstance = require('./utils/fetchLobby.js');
const storeGameData = require('./utils/storeGameData.js');
const fetchGameDataInstance = require('./utils/fetchGame.js');
const fetchLobbyGameData = require('./utils/fetchLobbyGame.js');
const positionAvail = require('./utils/positionUpdate.js');
// Print a message to the console indicating that the gerver is running
console.log('Server is running');

// Create a new Socket.IO instance and define variables to track player count
const io = socket(server);
var playercounter = 0;
var clientsList = [];
var cheaterList = [];
var lobbies = [];
var gameData = [];

//********************************************************************************************************** */
//                          ***Connection Handling begins here***                                            */
//********************************************************************************************************** */
io.on('connection', (socket) => {
    var lobbycode = 0, registered = 0;

    // Increment the player counter and log a message to the console (player counter does not indicate registered, just client connected to the server)
    playercounter++;
    console.log("New connection: " + socket.id);

    // If the maximum number of players has been reached, refuse the connection and log a message
    if (playercounter > settings.MAX_PLAYERS) {
        socket.disconnect(true);
        playercounter--;
        console.error('[Server] Refused connection to player, reason: player count too high!');
    } else {
        // Otherwise, log a message indicating that a player has connected
        console.log('[Server] A player connected!\nCurrently ' + playercounter + '/'+settings.MAX_PLAYERS+' online!');
    }
    //********************************************************************************************************** */
    //  CURRENT LISTENERS:
    //      "alive"         -   serves as a ping to the gerver, may be used by the client
    //      "register"      -   registers client on gerver with a name (lets see how this is going in the future)
    //      "getplayers"    -   get playercount
    //      "createlobby"   -   create a lobby with a code(six digit number)
    //      "getplayerlist" -   gerver sends all clients of the gerver
    //      "joinlobby"     -   join a lobby with a code (six digit number)
    //      "playonline"    -   check if playing online is possible for the client(ambigious, may be removed in the future)
    //      "move"          -   handle movement(may get bigger in the future, currently only handles movement of rabbits)
    //********************************************************************************************************** */
    //                          ***PLEASE PUT YOUR LISTENERS/EMITTERS BELOW HERE***                              */
    //********************************************************************************************************** */

    //********************************************************************************************************** */
    //                          ***Basic gerver functions below here***                                          */
    //********************************************************************************************************** */

    // Ping (Broadcast asynchronous)
    socket.on('alive', async () => {
        console.log('[Server] Server is up and running!');
        await io.emit('alive', 1);
    });

    // Listen for requests to get the player count and emit the count to all clients (Broadcast)
    socket.on('getplayers', () => {
        console.log('[Server] Sending player information!');
        io.emit('getplayers', playercounter);
    });

    // Register with name for identification
    socket.on('register', args => {
        var taken = 0, loggedin=0;
        for (var i = 0; i < clientsList.length; i++) {
            if (clientsList[i].name == args) taken = 1;
            if (clientsList[i].id == socket.id) loggedin = 1;
        }
        if (taken == 0 && loggedin == 0) {
            clientsList.push(storeClientInfo(socket.id, args));
            console.log('[Server] ALL PLAYERS\n\t'+JSON.stringify(clientsList));
            socket.to(socket.id).emit("register", 1);
            registered = 1;
        } else {
            console.error('[Server] Blocked an ambgigious name registering action! (error 400)');
            // Emit errorCode 400 - Name already taken
            socket.to(socket.id).emit('error', 400);
        }

    });


    socket.on('getplayerlist', () => {
        console.log('[Server] Sending player list information!');
        io.to(lobbycode).emit('getplayerlist', clientsList);
    });

    //********************************************************************************************************** */
    //                          ***Lobby and Online Logic below here***                                          */
    //********************************************************************************************************** */

    // Create sub lobby on this gerver with a code
    socket.on('createlobby', code => {
        if (code.length !== 6 || playerExist(clientsList, socket.id) === 0 || lobbyExist(lobbies, code) === 1) {
            io.to(socket.id).emit('error', 300);
            console.error("[Server] Error while creating lobby (error 300)");
        } else {
            console.log("[Server] Lobby creation");
            lobbies.push(storeLobbyInfo(code, socket.id, 0));
            lobbies[lobbies.length-1].players.push(fetchClientInstance(clientsList, socket.id));

            console.log("[Server] Lobby saved!\nALL LOBBIES\n\t"+JSON.stringify(lobbies));
            socket.join(code);
            lobbycode = code;
            saveGameData(socket.id, lobbycode);
            io.to(socket.id).emit("createlobby", 1);
        }
    });

    // Join a lobby on this gerver with the lobby code
    socket.on('joinlobby', code=>{
        if (code.length !== 6 || playerExist(clientsList, socket.id) === 0 || lobbyExist(lobbies, code) === 0) {
            io.to(socket.id).emit('error', 302);
            console.log("[Server] Error joining lobby (error 302)");
        } else {
            const lobby = fetchLobbyInstance(lobbies, code);
            const playerExists = playerExist(lobby.players, socket.id);
            playerExists ? (
                io.to(socket.id).emit('error', 302),
                console.error("[Server] Very funny... Player is already in the lobby can't join double!\nABORT JOINING")
            ) : (
                lobby.players.push(fetchClientInstance(clientsList, socket.id)),
                console.log(JSON.stringify(lobbies[0])),
                socket.join(code),
                lobbycode = code,
                saveGameData(socket.id, lobbycode),
                console.log("[Server] Player " + socket.id + " joins lobby " + code)
            );
        }
    });

    // Function for clients to press the "Multiplayer" button and get a feedback if its even possible, ofcourse they will get the feedback anyway but this is faster
    socket.on('playonline', code => {
        if (playerExist(clientsList, socket.id) == 0) {
            io.emit('error', 401);
            console.error("[Server] Refused to let a client play online, because no username was registered beforehand (error 401)");
        }
        else {
            console.log("[Server] Granted a player the wish to player online!");
            io.to(socket.id).emit("playonline", 1);
        }
    });

    socket.on('getplayerslobby', args => {
        if(lobbycode.length === 6){
            lobby = fetchLobbyInstance(lobbies, lobbycode);
            console.log("[Server] PlayerList\n\t"+JSON.stringify(lobby.players));

            io.to(socket.id).emit("getplayerslobby", JSON.stringify(lobby.players));
    }
    });

    //********************************************************************************************************** */
    //                          ***Game Logic handling below here***                                             */
    //              From here on out there will be no suffisticated checks if the login and stuff is in order    */
    //********************************************************************************************************** */

    // Move logic, Client must handle the logic accordingly
    // Please don't forget rabbit1 = 0, rabbit2 = 1, rabbit3 = 2, rabbit4 = 3
    socket.on('move', (steps, rabbit) =>{   
        if(registered === 1 && lobbycode !== 0 && steps < 8){
            var game = fetchGameDataInstance(gameData, socket.id);

            var newpos = game.rabbits[parseInt(rabbit)].position + parseInt(steps);
            gameData = positionAvail(gameData, newpos);
            game.rabbits[parseInt(rabbit)].position = newpos;

            io.to(lobbycode).emit("move", fetchLobbyGameData(gameData, lobbycode));
            console.log("[Server] Player "+fetchClientInstance(clientsList, socket.id)+" is moving "+steps+" steps with rabbit "+rabbit+"!");
        }else{
            console.error("[Server] Invalid move!")
            io.to(socket.id).emit("error", 500);
        }
    });
     socket.on('moveCheat', (pos, rabbit) =>{
            if(registered === 1 && lobbycode !== 0 ){
                var game = fetchGameDataInstance(gameData, socket.id);
                var newpos = pos;
                gameData = positionAvail(gameData, newpos);
                game.rabbits[parseInt(rabbit)].position= parseInt(pos);


                io.to(lobbycode).emit("moveCheat", fetchLobbyGameData(gameData, lobbycode));
                console.log("[Server] Player "+fetchClientInstance(clientsList, socket.id)+" moved to the "+pos+" position with rabbit "+rabbit+"!");
                   console.log("[Server] Player is on "+parseInt(pos)+"!");

            }else{
                console.error("[Server] Invalid move!")
                io.to(socket.id).emit("error", 500);
            }
        });
    //Shake-Sensor, notifying each player once event occurs.
    socket.on('shake', args=>{
        io.to(lobbycode).emit('shake', socket.id);
    });

    //Carrotspin, notifying Client the carrot has been spun
    //socket.on('carrotspinning', args=>{
        //const randomField = Math.floor(Math.random() * 10);
       // console.log('Random Field (hole):', randomField);
        //io.to(lobbycode).emit('carrotspinning', socket.id, randomField);
   // });
    socket.on('carrotspin', (lobbycode) => {
        const randomhole = Math.floor(Math.random() * 11);
        let lobbyIndex = lobbies.findIndex(lobby => lobby.code === lobbycode);
        if (lobbyIndex !== -1) {
            lobbies[lobbyIndex].hole = randomhole;
            console.log(`[Server] Lobby ${lobbycode}'s hole updated to ${randomhole}`);
            io.to(lobbycode).emit('carrotspin', socket.id, randomhole);
        } else {
            console.error(`[Server] Lobby with code ${lobbycode} not found`);
        }
    });
    
    //Cheating, remark someone has cheated
        socket.on('cheat', args=>{

            console.log('User: ', args, "cheated");
            if(playerExist(cheaterList,socket.id)==0){
            clientsList.push(storeClientInfo(socket.id, args));
            }
            io.to(lobbycode).emit('cheat', socket.id);
        });
    //Hole appears below Rabbit logic
    socket.on('reset', (pos) => {
        var game = fetchGameDataInstance(gameData, socket.id);
        var targetPos = parseInt(pos);
       
        for (var i = 0; i < game.rabbits.length; i++) {
            if (game.rabbits[i].position === targetPos) {
                gameData = positionAvail(gameData, 0);
                game.rabbits[parseInt(i)].position = 0;
                break;
            }
        }
        io.to(lobbycode).emit("move", fetchLobbyGameData(gameData, lobbycode));
    });


    //********************************************************************************************************** */
    //***PLEASE PUT YOUR LISTENERS/EMITTERS ABOVE HERE***                                                        */            
    //********************************************************************************************************** */
    // Listen for disconnection events and log a message to the console
    socket.on('disconnect', () => {
        playercounter -= 1;
        const index = clientsList.findIndex(client => client.clientId === socket.id);

        // unregister lobby if owner disconnects
        if(lobbies) {
            var lobbyindex = lobbies.findIndex(lobby => lobby.owner === socket.id);
            if(lobbyindex !== -1) {
                lobbies.splice(lobbyindex, 1);  // splice lobby from lobbies
                for(var i = 0; i<gameData.length; i++){
                    if(gameData[i].lobbycode === lobbycode){
                        gameData.splice(i, 1);  // splice gameData of lobby from gameData
                    }
                }
            console.log('[Server] Owner disconnected, lobby deleted!\n\t'+JSON.stringify(lobbies));
            }
        }
        if (index !== -1) { // Remove client from clientsList
            clientsList.splice(index, 1);
            console.log('[Server] Player Unregistered, id: '+socket.id);
        }
        console.log('[Server] A player disconnected!\nCurrently ' + playercounter + ' online!');
    });
});

    //********************************************************************************************************** */
    //                          ***Functions below here***                                                       */
    //********************************************************************************************************** */

    // Function to save the game data with log of what is happening, may be removed in the future
    function saveGameData(socketid, lobbycode){
        var gdCurr = fetchLobbyGameData(gameData, lobbycode);
        const usedColors = [];
       // rabbitcolor = 'white';
        gdCurr.forEach(game => {
            // Check if the game's color property is already in the usedColors array
            if (!usedColors.includes(game.color)) {
              // If not, add it to the usedColors array
              usedColors.push(game.color);
            }
          });
          console.log("[Server] Game data sucessfully created"+usedColors.toString());
          // Fowler would be proud
          if (!usedColors.includes('white')) {
            rabbitcolor = 'white';
          }else if (!usedColors.includes('red')) {
            rabbitcolor = 'red';
          }else if (!usedColors.includes('pink')) {
            rabbitcolor = 'pink';
          }else if (!usedColors.includes('green')) {
            rabbitcolor = 'green';
          }

        gameDataTemp = (storeGameData(socketid, lobbycode, rabbitcolor));
        console.log("[Server] Game data sucessfully created"+JSON.stringify(gameDataTemp));
        gameData.push(gameDataTemp);
        console.log("[Server] Game data saved!\n");
    }