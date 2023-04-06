// Print a startup message to the console
console.log('Lotti Karotti Server\nServer is booting up, please check log for failure!\n');

// Import the 'fs' module and read the contents of the 'settings.json' file
const fs = require('fs');
const settingsData = fs.readFileSync('utils/settings.json');
const settings = JSON.parse(settingsData);

// Import the 'express' and 'socket.io' modules and create a new Express app
const express = require('express');
const socket = require('socket.io');
const app = express();

// Define the port number for the server
var PORT = process.env.PORT || 3000;

// Start the server and serve static files from the 'public' directory
const server = app.listen(PORT);
app.use(express.static('public'));

// Print a message to the console indicating that the server is running
console.log('Server is running');

// Create a new Socket.IO instance and define variables to track player count
const io = socket(server);
var count = 0;
var playercounter = 0;

// Listen for incoming connections from clients
io.on('connection', (socket) => {
    // Increment the player counter and log a message to the console
    playercounter++;
    console.log("New connection: " + socket.id);

    // If the maximum number of players has been reached, refuse the connection and log a message
    if (playercounter > settings.MAX_PLAYERS) {
        socket.disconnect(true);
        playercounter--;
        console.log('Server refused connection to player, reason: player count too high!\n');
    } else {
        // Otherwise, log a message indicating that a player has connected
        console.log('A player connected!\nCurrently ' + playercounter + ' online!');
    }

    socket.on('alive', () => {
        console.log('[Server] Server is up and running!');
        io.emit('alive', 1);
    })

    // Listen for requests to get the player count and emit the count to all clients
    io.on('getlpayers', () => {
        io.emit('getplayers', playercounter);
    });

    // Listen for disconnection events and log a message to the console
    socket.on('disconnect', () => {
        playercounter-=1;
        console.log('A player disconnected!\nCurrently ' + playercounter + ' online!');
    });
});

