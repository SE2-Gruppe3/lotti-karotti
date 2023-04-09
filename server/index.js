const server = require('./utils/server.js');
const settings = require('./utils/settings.js');
const socket = require('./utils/socket.js');
const storeClientInfo = require('./utils/storeClient.js');

// Print a message to the console indicating that the server is running
console.log('Server is running');

// Create a new Socket.IO instance and define variables to track player count
const io = socket(server);
var count = 0;
var playercounter = 0;
var clientsList = [];

// Listen for incoming connections from clients
io.on('connection', (socket) => {
    // Increment the player counter and log a message to the console
    playercounter++;
    console.log("New connection: " + socket.id);

    // If the maximum number of players has been reached, refuse the connection and log a message
    if (playercounter > settings.MAX_PLAYERS) {
        socket.disconnect(true);
        playercounter--;
        console.log('[Server] Refused connection to player, reason: player count too high!');
    } else {
        // Otherwise, log a message indicating that a player has connected
        console.log('[Server] A player connected!\nCurrently ' + playercounter + ' online!');
    }
    //********************************************************************************************************** */
    //  CURRENT LISTENERS:
    //      "alive"         -   serves as a ping to the server, may be used by the client
    //      "register"      -   registers client on server with a name (lets see how this is going in the future)
    //      "getplayers"    -   get all the current players
    //********************************************************************************************************** */
    //                          ***PLEASE PUT YOUR LISTENERS/EMITTERS BELOW HERE***                              */
    //********************************************************************************************************** */
    
    socket.on('alive', () => {
        console.log('[Server] Server is up and running!');
        io.emit('alive', 1);
    })

    socket.on('register', args => {
        taken = 0;
        for (var i = 0; i < clientsList.length; i++) {
            if (clientsList[i].name == args) taken = 1
        }
        if (taken == 0) {
            clientsList.push(storeClientInfo(socket.id, args));
            console.log('[Server] New client has registered with the name \'' + args + '\'');
        } else {
            console.log('[Server] Blocked an ambgigious name registering action!\nNAME ALREADY TAKEN')
            // Emit errorCode 400 - Name already taken
            socket.emit('error', 400);
        }

    });

    // Listen for requests to get the player count and emit the count to all clients
    io.on('getlpayers', () => {
        io.emit('[Server] getplayers', playercounter);
    });

    //********************************************************************************************************** */
    //***PLEASE PUT YOUR LISTENERS/EMITTERS ABOVE HERE*** */
    //********************************************************************************************************** */
    // Listen for disconnection events and log a message to the console
    socket.on('disconnect', () => {
        playercounter -= 1;
        console.log('[Server] A player disconnected!\nCurrently ' + playercounter + ' online!');
    });
});

