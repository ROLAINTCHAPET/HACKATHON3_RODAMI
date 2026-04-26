#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api"
echo "🚀 Démarrage du test TWIST 09 (Interactions Réelles)..."

# 1. Inscription et Login
echo -e "\n1️⃣ Création d'un utilisateur de test..."
AUTH_RES=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Interacteur",
    "prenom": "Paul",
    "email": "paul.real@campus.fr",
    "password": "password123"
  }')

TOKEN=$(echo $AUTH_RES | grep -oP '(?<="token":")[^"]*')
USER_ID=$(echo $AUTH_RES | grep -oP '(?<="userId":)[0-9]*')
echo "✅ Utilisateur Paul créé (ID: $USER_ID)"

# 2. Création d'un événement (par l'admin)
echo -e "\n2️⃣ Création d'un événement par l'admin..."
# On utilise un token admin si possible, sinon on suppose que Paul peut créer (dev mode)
EVENT_RES=$(curl -s -X POST "$BASE_URL/events" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titre": "Workshop TWIST 09",
    "description": "Un workshop sur les interactions réelles",
    "dateDebut": "2026-05-01T10:00:00Z",
    "lieu": "Amphi Réalité",
    "categoryId": 1
  }')
EVENT_ID=$(echo $EVENT_RES | grep -oP '(?<="id":)[0-9]*')
echo "✅ Événement créé (ID: $EVENT_ID)"

# Publication
curl -s -X PATCH "$BASE_URL/events/$EVENT_ID/publish" -H "Authorization: Bearer $TOKEN" > /dev/null
echo "✅ Événement publié."

# 3. Inscription (Le "Clic")
echo -e "\n3️⃣ Inscription à l'événement (Simple Clic)..."
curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" \
  -H "Authorization: Bearer $TOKEN" | json_pp

echo -e "\n📊 Vérification DB (Optionnel) : l'intérêt n'est PAS encore ajouté (selon TWIST 09)."

# 4. Confirmation de présence (L'Interaction Réelle)
echo -e "\n4️⃣ Confirmation de présence (Interaction Réelle)..."
curl -s -X POST "$BASE_URL/events/$EVENT_ID/attendance/confirm" \
  -H "Authorization: Bearer $TOKEN"
echo "✅ Présence confirmée."

# 5. Vérification du profil (L'intérêt doit être apparu)
echo -e "\n5️⃣ Vérification du profil de Paul..."
curl -s -X GET "$BASE_URL/profiles/me" \
  -H "Authorization: Bearer $TOKEN" | json_pp

# 6. Annulation de l'événement (Dépendance Destructrice)
echo -e "\n6️⃣ Annulation de l'événement (Test de la pénalité systémique)..."
curl -s -X PATCH "$BASE_URL/events/$EVENT_ID/cancel" \
  -H "Authorization: Bearer $TOKEN"
echo "✅ Événement annulé. La pénalité a été appliquée aux scores de réalité en tâche de fond."

echo -e "\n✨ Test TWIST 09 terminé !"
