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
const fs = require('fs');
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
const { log, Console } = require('console');
// Print a message to the console indicating that the gerver is running
console.log('Server is running');

// Create a new Socket.IO instance and define variables to track player count
const io = socket(server);
// Holes for the standard map, when used -> map to index 0-10
const holes = [0, 3, 5, 7, 9, 12, 17, 19, 22, 25, 27];

var playercounter = 0;
var clientsList = [];
var cheaterList = [];
var lobbies = [];
var gameData = [];
var votes = 1;
var percentage = 0;
var voterStarterId = 0;
var cheaterId = 0;

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
        console.log('[Server] A player connected!\nCurrently ' + playercounter + '/' + settings.MAX_PLAYERS + ' online!');
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
        var taken = 0, loggedin = 0;
        for (var i = 0; i < clientsList.length; i++) {
            if (clientsList[i].name == args) taken = 1;
            if (clientsList[i].id == socket.id) loggedin = 1;
        }
        if (taken == 0 && loggedin == 0) {
            clientsList.push(storeClientInfo(socket.id, args));
            console.log('[Server] ALL PLAYERS\n\t' + JSON.stringify(clientsList));
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

    socket.on('startgame', () => {
        console.log('[Server] Flagging game as started!');
        var lobby = fetchLobbyInstance(lobbies, lobbycode);
        if (lobby.game_started == 0) lobby.game_started = 1;
        io.to(lobbycode).emit('startgame', 1);
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
            lobbies.push(storeLobbyInfo(code, socket.id, 0, "classic"));
            lobbies[lobbies.length - 1].players.push(fetchClientInstance(clientsList, socket.id));

            console.log("[Server] Lobby saved!\nALL LOBBIES\n\t" + JSON.stringify(lobbies));
            socket.join(code);
            lobbycode = code;
            saveGameData(socket.id, lobbycode);
            io.to(socket.id).emit("createlobby", 1);
            // Set turn true for owner
            io.to(socket.id).emit('turn', socket.id);
        }
    });

    // Join a lobby on this gerver with the lobby code
    socket.on('joinlobby', code => {
        if (code.length !== 6 || playerExist(clientsList, socket.id) === 0 || lobbyExist(lobbies, code) === 0) {
            io.to(socket.id).emit('error', 302);
            console.log("[Server] Error joining lobby (error 302)");
        } else {
            const lobby = fetchLobbyInstance(lobbies, code);
            const playerExists = playerExist(lobby.players, socket.id);
            if (lobby.game_started == 1) io.to(socket.id).emit('error', 303);
            else {
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
        if (lobbycode.length === 6) {
            lobby = fetchLobbyInstance(lobbies, lobbycode);
            console.log("[Server] PlayerList\n\t" + JSON.stringify(lobby.players));

            io.to(socket.id).emit("getplayerslobby", JSON.stringify(lobby.players));
        }
    });

    socket.on('gethighscore', () => {
        fs.readFile('highscore.json', 'utf8', (err, data) => {
            if(err) {
                console.log("Error loading file!");
            }
            const jsonData = JSON.parse(data);
            console.log('[Server] Sending players highscore information!');
        io.emit('gethighscore', jsonData);
        });
    });

    socket.on('saveupdatedhighscore', (jsonArray) => {
        try {
            fs.writeFile('highscore.json', JSON.stringify(jsonArray), (err) => {
                if (err) throw err;
                console.log('[Server] Successfully saved updated Highscore list');
                socket.emit('saveJsonSuccess');
            });
        } catch (err) {
            console.error('[Server] Error while updating highscore list');
            socket.emit('saveJsonError', 'Invalid JSON array');
        }
    });  

    //********************************************************************************************************** */
    //                          ***Game Logic handling below here***                                             */
    //              From here on out there will be no suffisticated checks if the login and stuff is in order    */
    //********************************************************************************************************** */

    // Move logic, Client must handle the logic accordingly
    // Please don't forget rabbit1 = 0, rabbit2 = 1, rabbit3 = 2, rabbit4 = 3
    socket.on('move', (steps, rabbit) => {
        if (registered === 1 && lobbycode !== 0 && steps < 8) {
            var game = fetchGameDataInstance(gameData, socket.id);
            var currLobby = fetchLobbyInstance(lobbies, lobbycode);

            var newpos = game.rabbits[parseInt(rabbit)].position + parseInt(steps);

           

            // If player lands on a hole, set position to 0
            if (newpos === currLobby.hole || newpos === currLobby.holeTwo) newpos = 0;

            gameData = positionAvail(gameData, newpos);
            game.rabbits[parseInt(rabbit)].position = newpos;

             // If player lands on final field, he wins and game ends
             if (newpos === 29) {
                console.log("[Server] Player " + JSON.stringify(fetchClientInstance(clientsList, socket.id)) + " is moving " + steps + " steps with rabbit " + rabbit + "!");
                console.log("[Server] Player " + JSON.stringify(fetchClientInstance(clientsList, socket.id)) + " moved to field 29, they won with rabbit " + rabbit + "!");
                var winner = JSON.stringify(fetchClientInstance(clientsList, socket.id).name);
                io.to(lobbycode).emit("winning", winner);

            }

            io.to(lobbycode).emit("move", fetchLobbyGameData(gameData, lobbycode));
            console.log("[Server] Player " + JSON.stringify(fetchClientInstance(clientsList, socket.id)) + " is moving " + steps + " steps to field " + newpos + " with rabbit " + rabbit + "!");

            setTurn();
        } else {
            console.error("[Server] Invalid move!")
            io.to(socket.id).emit("error", 500);
        }
    });

    socket.on('moveCheat', (pos, rabbit) => {
        if (registered === 1 && lobbycode !== 0) {
            var game = fetchGameDataInstance(gameData, socket.id);
            var newpos = pos;
            gameData = positionAvail(gameData, newpos);
            game.rabbits[parseInt(rabbit)].position = parseInt(pos);


            io.to(lobbycode).emit("moveCheat", fetchLobbyGameData(gameData, lobbycode));
            setTurn();
            console.log("[Server] Player " + fetchClientInstance(clientsList, socket.id) + " moved to the " + pos + " position with rabbit " + rabbit + "!");
            console.log("[Server] Player is on " + parseInt(pos) + "!");

        } else {
            console.error("[Server] Invalid move!")
            io.to(socket.id).emit("error", 500);
        }
    });
    //Shake-Sensor, notifying each player once event occurs.
    socket.on('shake', args => {
        io.to(lobbycode).emit('shake', socket.id);
    });

    //Notify each player when voting starts
    socket.on('createvotingpopup', accusedPlayer => {
        voterStarterId = socket.id;
        var exists = 0;
        for (var i = 0; i < clientsList.length; i++) {
            if (clientsList[i].name == accusedPlayer) exists = 1;
        }
        if (exists === 1) {
            io.to(lobbycode).emit('createvotingpopup', socket.id, accusedPlayer);
        }
        else {
            io.to(lobbycode).emit('createvotingpopup', socket.id, "Error");
        }
    })

    socket.on('vote', args => {
        votes++;
        console.log("[SERVER] Player just voted yes. Number of yes votes: " + votes)
        io.to(lobbycode).emit('vote', votes);
    })

    socket.on('getvotingresult', args => {
        percentage = (votes / playercounter) * 100
        votes = 1
        console.log("[SERVER] Voting results are Yes: " + percentage)
        io.to(lobbycode).emit('getvotingresult', percentage)
    })

    //Carrotspin, notifying Client the carrot has been spun
    socket.on('carrotspin', args => {
        var lobby = fetchLobbyInstance(lobbies, lobbycode);

        if (lobby !== undefined) {
            if (lobby.mutator === "classic") {
                const randomhole = Math.floor(Math.random() * 11);
                lobby.hole = holes[randomhole];
                console.log(`[Server] Lobby ${lobbycode}'s hole updated to ${randomhole}`);
                io.to(lobbycode).emit('carrotspin', randomhole);

                setTurn();

            } else if (lobby.mutator === "spicyCarrot") {
                const randomhole = Math.floor(Math.random() * 11);
                lobby.hole = holes[randomhole];
                const temp = Math.floor(Math.random() * 11);
                const randomholeTwo = (randomhole + temp + 1) % 10;
                lobby.holeTwo = holes[randomholeTwo]
                console.log(`[Server] Lobby ${lobbycode}'s hole one updated to ${randomhole}`);
                console.log(`[Server] Lobby ${lobbycode}'s hole two updated to ${randomholeTwo}`);
                io.to(lobbycode).emit('spicycarrotspin', randomhole, randomholeTwo);

                setTurn();

            } else {
                console.error(`[Server] Lobby with code ${lobbycode} seems to have an undefined Gamemode`);
            }

        } else {
            console.error(`[Server] Lobby with code ${lobbycode} not found`);
        }
    });

    //getHole. A method to receive the current hole on the Map for a specific server
    socket.on('gethole', (lobbycode, desiredPos, rabbit) => {
        let lobbyIndex = lobbies.findIndex(lobby => lobby.code === lobbycode);
        if (lobbyIndex !== -1) {
            var currHole = lobbies[lobbyIndex].hole;
            console.log(`[Server] Lobby ${lobbycode}'s hole is currently ${currHole}`);
            io.to(lobbycode).emit('gethole', fetchLobbyGameData(gameData, lobbycode), currHole, desiredPos, rabbit);
        } else {
            console.error(`[Server] Lobby with code ${lobbycode} not found`);
        }
    });

    //setMutator, sets the desired Mutator to the corresponding Lobby
    socket.on('setMutator', (mutator) => {
        var lobby = fetchLobbyInstance(lobbies, lobbycode);
        if (lobby !== undefined) {
            lobby.mutator = mutator;
            console.log(`[Server] Lobby ${lobbycode}'s Mutator updated to ${mutator}`);
            io.to(lobbycode).emit('mutatorSelected');
        } else {
            console.error(`[Server] Lobby with code ${lobbycode} not found`);
        }
    });
    //getMutator, called by nonHosts to check if the mode has been selected already. Can be adjusted and used for the second Mutator, for now it works for spicyCarrot
    socket.on('getMutator', args => {
        var lobby = fetchLobbyInstance(lobbies, lobbycode);
        if (lobby !== undefined) {
            const currMutator = lobby.mutator;
            if (currMutator !== undefined) {
                if (currMutator === "spicyCarrot") {
                    io.to(lobbycode).emit('getMutator', "spicyCarrot");
                } //insert else if for second Mutator
            } else if(currMutator === "specialCard"){
                io.to(lobbycode).emit('getMutator', "specialCard");
            } else { //if Mutator == classic
                io.to(lobbycode).emit('getMutator', "classic");
            }
        } else {
            console.error(`[Server] Lobby with code ${lobbycode} not found`);
        }
    });

    socket.on('hostTurn', args => {
        console.log("[SERVER] Host has turned");
        setTurn();
    })


    //Cheating, remark someone has cheated
    socket.on('cheat', args => {

        console.log('User: ', args, "cheated");
        cheaterList.push(socket.id);
        console.log("[SERVER] " + cheaterList);
        io.to(lobbycode).emit('cheat', socket.id);
    });

 //Cheating, remark someone has cheated
    socket.on('isTurnOf', args => {

        console.log('Turn of: ', args);

        io.to(lobbycode).emit('isTurnOf', args);
    });
    socket.on('checkifplayercheated', args => {
        var playerExists = false;
        var cheated = false;

        for (var i = 0; i < clientsList.length; i++) {
            if (clientsList[i].name == args){
                playerExists = true;
                cheaterId = clientsList[i].clientId;
            }
        }

        for (var i = 0; i < cheaterList.length; i++) {
            if (cheaterList.includes(cheaterId)) cheated = true;
        }

        if (playerExists === true && cheated === true) {
            console.log("[SERVER] Player cheated");
            io.to(lobbycode).emit('checkifplayercheated', "true", percentage);
        }
        else {
            console.log("[SERVER] Player did not cheated");
            io.to(lobbycode).emit('checkifplayercheated', "false", percentage);
        }
    });

    //Remove players rabbits as punishment after voting
    socket.on('resetallrabittsfromplayer', args => {
        var playerExists = false;
        var cheated = false;
        var game;
        var pos0 = 0;
        var pos1 = 0;
        var pos2 = 0;
        var pos3 = 0;

        // for (var i = 0; i < clientsList.length; i++) {
        //     if (clientsList[i].name == args){
        //         playerExists = true;
        //         id = clientsList[i].clientId;
        //     }
        // }

        for (var i = 0; i < cheaterList.length; i++) {
            if (cheaterList.includes(cheaterId)) {
                cheated = true;
            }
        }
        console.log("Ch "+cheated + " id "+cheaterId);
        if (cheated === true && cheaterId != 0) {
            var game = fetchGameDataInstance(gameData, cheaterId);

            for (var i = 0; i < game.rabbits.length; i++) {
                if(pos0 == 0){
                    pos0 = game.rabbits[parseInt(i)].position;
                }
                else if(pos1 == 0){
                    pos1 = game.rabbits[parseInt(i)].position;
                }
                else if(pos2 == 0){
                    pos2 = game.rabbits[parseInt(i)].position;
                }
                else if(pos3 == 0){
                    pos3 = game.rabbits[parseInt(i)].position;
                }
            }
            console.log("POS0 "+pos0);
            console.log("POS1 "+pos1);
            console.log("POS2 "+pos2);
            console.log("POS3 "+pos3);

            for (var i = 0; i < game.rabbits.length; i++) {
                if (game.rabbits[i].position === pos0 || game.rabbits[i].position === pos1 || game.rabbits[i].position === pos2 || game.rabbits[i].position === pos3) {
                    gameData = positionAvail(gameData, 0);
                    game.rabbits[parseInt(i)].position = 0;
                    break;
                }
            }
            io.to(lobbycode).emit("move", fetchLobbyGameData(gameData, lobbycode));
            setTurn();
            cheaterList.pop(cheaterId);
            cheaterId = 0;
            voterStarterId = 0;
        }

        else if(voterStarterId != 0) {
            var game = fetchGameDataInstance(gameData, voterStarterId);

            for (var i = 0; i < game.rabbits.length; i++) {
                if(pos0 == 0){
                    pos0 = game.rabbits[parseInt(i)].position;
                }
                else if(pos1 == 0){
                    pos1 = game.rabbits[parseInt(i)].position;
                }
                else if(pos2 == 0){
                    pos2 = game.rabbits[parseInt(i)].position;
                }
                else if(pos3 == 0){
                    pos3 = game.rabbits[parseInt(i)].position;
                }
            }
            console.log("POS0 "+pos0);
            console.log("POS1 "+pos1);
            console.log("POS2 "+pos2);
            console.log("POS3 "+pos3);

            for (var i = 0; i < game.rabbits.length; i++) {
                if (game.rabbits[i].position === pos0 || game.rabbits[i].position === pos1 || game.rabbits[i].position === pos2 || game.rabbits[i].position === pos3) {
                    gameData = positionAvail(gameData, 0);
                    game.rabbits[parseInt(i)].position = 0;
                    break;
                }
            }
            io.to(lobbycode).emit("move", fetchLobbyGameData(gameData, lobbycode));
            setTurn();
            voterStarterId = 0;
        }
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
        setTurn();
    });

    //********************************************************************************************************** */
    //***PLEASE PUT YOUR LISTENERS/EMITTERS ABOVE HERE***                                                        */            
    //********************************************************************************************************** */

    function setTurn() {
        var lobby = fetchLobbyInstance(lobbies, lobbycode);
        // Sending out turn to next player
        lobby.socket_turn++;
        if (lobby.socket_turn == lobby.players.length) lobby.socket_turn = 0;

        io.to(lobby.players[lobby.socket_turn].clientId).emit('turn', lobby.players[lobby.socket_turn].clientId);
        console.log("[Server] Next turn is id " + lobby.players[lobby.socket_turn].clientId);
    }


    // Listen for disconnection events and log a message to the console
    socket.on('disconnect', () => {
        playercounter -= 1;
        const index = clientsList.findIndex(client => client.clientId === socket.id);

        // unregister lobby if owner disconnects
        if (lobbies) {
            var lobbyindex = lobbies.findIndex(lobby => lobby.owner === socket.id);
            if (lobbyindex !== -1) {
                lobbies.splice(lobbyindex, 1);  // splice lobby from lobbies
                for (var i = 0; i < gameData.length; i++) {
                    if (gameData[i].lobbycode === lobbycode) {
                        gameData.splice(i, 1);  // splice gameData of lobby from gameData
                    }
                }
                console.log('[Server] Owner disconnected, lobby deleted!\n\t' + JSON.stringify(lobbies));
            }
        }
        if (index !== -1) { // Remove client from clientsList
            clientsList.splice(index, 1);
            console.log('[Server] Player Unregistered, id: ' + socket.id);
        }
        console.log('[Server] A player disconnected!\nCurrently ' + playercounter + ' online!');
    });
});

//********************************************************************************************************** */
//                          ***Functions below here***                                                       */
//********************************************************************************************************** */

// Function to save the game data with log of what is happening, may be removed in the future
function saveGameData(socketid, lobbycode) {
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
    console.log("[Server] Game data sucessfully created" + usedColors.toString());
    // Fowler would be proud
    if (!usedColors.includes('white')) {
        rabbitcolor = 'white';
    } else if (!usedColors.includes('red')) {
        rabbitcolor = 'red';
    } else if (!usedColors.includes('pink')) {
        rabbitcolor = 'pink';
    } else if (!usedColors.includes('green')) {
        rabbitcolor = 'green';
    }

    gameDataTemp = (storeGameData(socketid, lobbycode, rabbitcolor));
    console.log("[Server] Game data sucessfully created" + JSON.stringify(gameDataTemp));
    gameData.push(gameDataTemp);
    console.log("[Server] Game data saved!\n");
}