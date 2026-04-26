import { initializeApp, getApps, getApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyDkq3dsrZjK6LZEn6XyrEKrrVt5F4n4Jps",
  authDomain: "hackathon3rodami.firebaseapp.com",
  projectId: "hackathon3rodami",
  storageBucket: "hackathon3rodami.firebasestorage.app",
  messagingSenderId: "830672508314",
  appId: "1:830672508314:web:5d1377d29615e1e4729623",
  measurementId: "G-PJ70GELMWG"
};

// Initialize Firebase
const app = getApps().length > 0 ? getApp() : initializeApp(firebaseConfig);
const auth = getAuth(app);

export { app, auth };
