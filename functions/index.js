const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

exports.registerUser = functions.https.onRequest(async (req, res) => { //definido dois http func
  // Handle user registration logic here
  // Use admin.firestore() to interact with Firestore
  // Send response using res.send()
});

exports.loginUser = functions.https.onRequest(async (req, res) => {
  // Handle user login logic here
  // Use admin.firestore() to interact with Firestore
  // Send response using res.send()
});
