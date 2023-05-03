// Print a startup message to the console
console.log('Lotti Karotti Server\nServer is booting up, please check log for failure!\n');

// Import the 'express' and 'socket.io' modules
const express = require('express');
const app = express();

// Define the port number for the server
var PORT = process.env.PORT || 3000;

// Start the server and serve static files from the 'public' directory
const server = app.listen(PORT);
app.use(express.static('public'));

// Export the 'server' object as a module
module.exports = server;