//const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/*
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  admin.database().ref('/messages').push({original: original}).then(snapshot => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    res.redirect(303, snapshot.ref);
  });
});

// Listens for new messages added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/messages/{pushId}/original')
    .onWrite(event => {
      // Grab the current value of what was written to the Realtime Database.
      const original = event.data.val();
      console.log('Uppercasing', event.params.pushId, original);
      const uppercase = original.toUpperCase() + "-New";
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return event.data.ref.parent.child('uppercase').set(uppercase);
    });

//	const wasDeleted = event.data.previous.child('isDeleted').val();
//  const isDeleted = event.data.current.child('isDeleted').val();
*/

exports.sumCowSteps = functions.database.ref('/trig_process/{cowId}')
	.onWrite(event => {
		const cowId = event.params.cowId;
		console.log('called function for cow ' + cowId);
		var res = event.data.forEach(function(child) {
			
			console.log('called child ' + child.key);
			
			var year = parseInt(child.key.substr(0, 4), 10);
			var month = parseInt(child.key.substr(4, 2), 10);
			var day = parseInt(child.key.substr(6, 2), 10);
			var hour = parseInt(child.key.substr(8, 2), 10);
			
			//child.ref.remove();
			
			
			admin.database().ref('/cow_data/'+cowId).child(year).child(month).child(day).child(hour).once('value').then(snapshot => {
				var total= 0;
				snapshot.forEach(function(hour) {
					if (hour.key != 'total_steps')
						total += hour.val();
				});
				admin.database().ref('/cow_data/'+cowId).child(year).child(month).child(day).child(hour).child('total_steps').set(total);
			}).then(() => {
				admin.database().ref('/cow_data/'+cowId).child(year).child(month).child(day).once('value').then(snapshot => {
					var total= 0;
					snapshot.forEach(function(day) {
						if (day.hasChildren()) {
							total += day.child('total_steps').val();
						}
					});
					admin.database().ref('/cow_data/'+cowId).child(year).child(month).child(day).child('total_steps').set(total);
				}).then(() => {
					admin.database().ref('/cow_data/'+cowId).child(year).child(month).once('value').then(snapshot => {
						var total= 0;
						snapshot.forEach(function(month) {
							if (month.hasChildren()) {
								total += month.child('total_steps').val();
							}
						});
						admin.database().ref('/cow_data/'+cowId).child(year).child(month).child('total_steps').set(total);
					}).then(() => {
						admin.database().ref('/cow_data/'+cowId).child(year).once('value').then(snapshot => {
							var total= 0;
							snapshot.forEach(function(year) {
								if (year.hasChildren()) {
									total += year.child('total_steps').val();
								}
							});
							admin.database().ref('/cow_data/'+cowId).child(year).child('total_steps').set(total);
						});
					});
				});
			});
			//updates[child.key] = null;
		});
		admin.database().ref('/trig_process/').child(cowId).remove();
		return res;
	});
