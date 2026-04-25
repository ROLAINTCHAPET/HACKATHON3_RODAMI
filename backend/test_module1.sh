#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api"
echo "🚀 Démarrage des tests du Module 1 (CampusLink)..."

# 1. Inscription (RF-01)
echo -e "\n1️⃣ Inscription d'un nouvel utilisateur..."
REGISTER_RES=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Testeur",
    "prenom": "Jean",
    "email": "jean.test@campus.fr",
    "password": "password123"
  }')

TOKEN=$(echo $REGISTER_RES | grep -oP '(?<="token":")[^"]*')
USER_ID=$(echo $REGISTER_RES | grep -oP '(?<="userId":)[0-9]*')

if [ -z "$TOKEN" ]; then
  echo "❌ Échec de l'inscription. Vérifiez que le backend tourne sur le port 8080."
  exit 1
fi
echo "✅ Utilisateur créé (ID: $USER_ID)"

# 2. Ajustement du Profil (Setup)
echo -e "\n2️⃣ Ajustement du profil (Intérêts + Filière)..."
curl -s -X POST "$BASE_URL/profiles/setup" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "interests": ["Sport", "Programmation", "Musique"],
    "filiere": "Informatique",
    "annee": 3
  }' | json_pp || echo "✅ Profil ajusté."

# 3. Récupération du Profil (RF-02)
echo -e "\n3️⃣ Récupération du profil complet (3 couches)..."
curl -s -X GET "$BASE_URL/profiles/me" \
  -H "Authorization: Bearer $TOKEN" | json_pp

# 4. Vérification de l'Historique (RF-05)
echo -e "\n4️⃣ Vérification de l'historique d'activité..."
curl -s -X GET "$BASE_URL/profiles/me/history" \
  -H "Authorization: Bearer $TOKEN" | json_pp

# 5. Suggestions Cold Start (TWIST 01)
echo -e "\n5️⃣ Vérification des suggestions Cold Start..."
curl -s -X GET "$BASE_URL/profiles/cold-start/suggestions" \
  -H "Authorization: Bearer $TOKEN" | json_pp

echo -e "\n✨ Tests terminés !"
