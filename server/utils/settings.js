// Import the 'fs' module for file system-related functionality
const fs = require('fs');

// Read the contents of the 'settings.json' file synchronously and store it in 'settingsData'
const settingsData = fs.readFileSync('utils/json/settings.json');

// Parse the contents of 'settingsData' as JSON and store the result in 'settings'
const settings = JSON.parse(settingsData);

// Export the 'settings' object as a module
module.exports = settings;