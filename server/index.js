//********************************************************************************************************** */
//               ***Gerver for Lotti Karotti, please use PM2 if launching Gerver professionally***           */
//                                   ***Imports section***                                                   */
//********************************************************************************************************** */
const server = require('./utils/server.js');
const settings = require('./utils/settings.js');
const socket = require('./utils/socket.js');
const fs = require('fs');
const storeClientInfo = require('./utils/storeClient.js');
const storeLobbyInfo = require('./utils/storeLobby.js');
const playerExist = require('./utils/checkPlayerExists.js');
const lobbyExist = require('./utils/checkLobbyExists.js');
const fetchClientInstance = require('./utils/fetchClient.js');
const fetchLobbyInstance = require('./utils/fetchLobby.js');

// Print a message to the console indicating that the server is running
console.log('Server is running');

// Create a new Socket.IO instance and define variables to track player count
const io = socket(server);
var playercounter = 0;
var clientsList = [];
var lobbies = [];

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
    //      "alive"         -   serves as a ping to the server, may be used by the client
    //      "register"      -   registers client on server with a name (lets see how this is going in the future)
    //      "getplayers"    -   get all the current players
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
    //                          ***Lobby and Online Logic below here***                                          */
    //********************************************************************************************************** */

    // Create sub lobby on this gerver with a code
    socket.on('createlobby', code => {
        if (code.length !== 6 || playerExist(clientsList, socket.id) === 0 || lobbyExist(lobbies, code) === 1) {
            io.to(socket.id).emit('error', 300);
            console.error("[Server] Error while creating lobby (error 300)");
        } else {
            console.log("[Server] Lobby creation");
            lobbies.push(storeLobbyInfo(code, socket.id));
            lobbies[lobbies.length-1].players.push(fetchClientInstance(clientsList, socket.id));

            console.log("[Server] Lobby saved!\nALL LOBBIES\n\t"+JSON.stringify(lobbies));
            socket.join(code);
            lobbycode = code;
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

    //********************************************************************************************************** */
    //                          ***Game Logic handling below here***                                             */
    //              From here on out there will be no suffisticated checks if the login and stuff is in order    */
    //********************************************************************************************************** */

    // Move logic, Client must handle the logic accordingly
    socket.on('move', (steps, rabbit) =>{
        if(registered === 1 && lobbycode !== 0 && steps < 8){
            io.to(lobbycode).emit("move", socket.id, steps);
            console.log("[Server] Player "+fetchClientInstance(clientsList, socket.id)+" is moving "+steps+" steps with rabbit "+rabbit+"!")
        }else{
            console.error("[Server] Invalid move!")
            io.to(socket.id).emit("error", 500);
        }
    });

    socket.on('drawcard', () => {
        let card = Math.floor(Math.random() * 4);
        console.log("[Server] Player drawed a card number " + card);
        io.to(lobbycode).emit('drawcard', card);
    });

    //********************************************************************************************************** */
    //***PLEASE PUT YOUR LISTENERS/EMITTERS ABOVE HERE***                                                        */            
    //********************************************************************************************************** */
    // Listen for disconnection events and log a message to the console
    socket.on('disconnect', () => {
        playercounter -= 1;
        const index = clientsList.findIndex(client => client.clientId === socket.id);
        if (index !== -1) {
            clientsList.splice(index, 1);
            console.log('[Server] Player Unregistered, id: '+socket.id);
        }
        console.log('[Server] A player disconnected!\nCurrently ' + playercounter + ' online!');
    });
});

