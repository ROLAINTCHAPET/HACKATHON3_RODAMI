import { 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword, 
  signOut, 
  onAuthStateChanged,
  User,
  getIdToken
} from "firebase/auth";
import { auth } from "../firebase/config";
import { OpenAPI } from "../core/OpenAPI";
import { AuthRequest } from "../models/AuthRequest";
import { RegisterRequest } from "../models/RegisterRequest";

export class AuthService {
  static async login(request: AuthRequest) {
    const userCredential = await signInWithEmailAndPassword(
      auth, 
      request.email!, 
      request.password!
    );
    return userCredential.user;
  }

  static async register(request: RegisterRequest) {
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth, 
        request.email!, 
        request.password!
      );
      return userCredential.user;
    } catch (error) {
      console.error("Firebase Registration Error:", error);
      throw error;
    }
  }

  static async logout() {
    await signOut(auth);
  }

  static onAuthChange(callback: (user: User | null) => void) {
    return onAuthStateChanged(auth, async (user) => {
      callback(user);
    });
  }

  static async getFreshToken(): Promise<string | null> {
    // If user is already there, return token with force refresh
    if (auth.currentUser) {
      try {
        return await getIdToken(auth.currentUser, true);
      } catch (e) {
        console.error("Token refresh failed:", e);
      }
    }

    // Otherwise, wait for auth to settle (max 2 seconds)
    return new Promise((resolve) => {
      const unsubscribe = onAuthStateChanged(auth, async (user) => {
        unsubscribe();
        if (user) {
          try {
            const token = await getIdToken(user, true);
            resolve(token);
          } catch (e) {
            console.error("Token retrieval failed:", e);
            resolve(null);
          }
        } else {
          resolve(null);
        }
      });
      setTimeout(() => {
        unsubscribe();
        resolve(null);
      }, 2000);
    });
  }
}
