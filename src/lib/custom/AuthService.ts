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
    const token = await getIdToken(userCredential.user);
    OpenAPI.TOKEN = token;
    return userCredential.user;
  }

  static async register(request: RegisterRequest) {
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth, 
        request.email!, 
        request.password!
      );
      const token = await getIdToken(userCredential.user);
      OpenAPI.TOKEN = token;
      return userCredential.user;
    } catch (error) {
      console.error("Firebase Registration Error:", error);
      throw error;
    }
  }

  static async logout() {
    await signOut(auth);
    OpenAPI.TOKEN = undefined;
  }

  static onAuthChange(callback: (user: User | null) => void) {
    return onAuthStateChanged(auth, async (user) => {
      if (user) {
        const token = await getIdToken(user);
        OpenAPI.TOKEN = token;
      } else {
        OpenAPI.TOKEN = undefined;
      }
      callback(user);
    });
  }

  static async getFreshToken() {
    if (auth.currentUser) {
      const token = await getIdToken(auth.currentUser, true);
      OpenAPI.TOKEN = token;
      return token;
    }
    return null;
  }
}
