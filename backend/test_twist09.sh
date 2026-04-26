#!/bin/bash

# Configuration
BASE_URL="http://localhost:8080/api"
echo "🚀 Démarrage du test TWIST 09 (Logique système respectée)..."

# Génération d'un ID unique pour cette session de test
RID=$RANDOM

# 1. Inscription et Login
echo -e "\n1️⃣ Création des utilisateurs..."
# Paul
PAUL_RES=$(curl -s -X POST "$BASE_URL/auth/register" -H "Content-Type: application/json" \
  -d "{\"nom\": \"Paul\", \"prenom\": \"Interacteur\", \"email\": \"paul.$RID@campus.fr\", \"password\": \"password123\"}")
PAUL_TOKEN=$(echo $PAUL_RES | grep -oP '(?<="token":")[^"]*')
echo "✅ Paul créé (paul.$RID@campus.fr)"

# Marie
MARIE_RES=$(curl -s -X POST "$BASE_URL/auth/register" -H "Content-Type: application/json" \
  -d "{\"nom\": \"Marie\", \"prenom\": \"Active\", \"email\": \"marie.$RID@campus.fr\", \"password\": \"password123\"}")
MARIE_TOKEN=$(echo $MARIE_RES | grep -oP '(?<="token":")[^"]*')
echo "✅ Marie créée (marie.$RID@campus.fr)"

# 2. Création et Publication d'un événement
echo -e "\n2️⃣ Création et Publication de l'événement..."
# On ajoute le LIEU pour satisfaire la validation TWIST 05
EVENT_RES=$(curl -s -X POST "$BASE_URL/events" -H "Authorization: Bearer $PAUL_TOKEN" -H "Content-Type: application/json" \
  -d "{\"titre\": \"Hackathon $RID\", \"description\": \"Workshop interactif\", \"dateDebut\": \"2026-05-01T10:00:00Z\", \"categoryId\": 1, \"lieu\": \"Bâtiment C\"}")
EVENT_ID=$(echo $EVENT_RES | grep -oP '(?<="id":)[0-9]*')

if [ -z "$EVENT_ID" ]; then
  echo "❌ Erreur création: $EVENT_RES"
  exit 1
fi

# Publication officielle
PUB_RES=$(curl -s -X PATCH "$BASE_URL/events/$EVENT_ID/publish" -H "Authorization: Bearer $PAUL_TOKEN")
echo "✅ Événement $EVENT_ID créé et publié."

# 3. Inscription
echo -e "\n3️⃣ Inscriptions..."
REG_PAUL=$(curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" -H "Authorization: Bearer $PAUL_TOKEN")
REG_MARIE=$(curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" -H "Authorization: Bearer $MARIE_TOKEN")
echo "✅ Paul et Marie inscrits."

# 4. Confirmation (TWIST 09)
echo -e "\n4️⃣ Confirmations de présence..."
echo "Marie arrive..."
curl -s -X POST "$BASE_URL/events/$EVENT_ID/attendance/confirm" -H "Authorization: Bearer $MARIE_TOKEN" > /dev/null
echo "Paul arrive (Déclenche l'Auto-Connect)..."
curl -s -X POST "$BASE_URL/events/$EVENT_ID/attendance/confirm" -H "Authorization: Bearer $PAUL_TOKEN" > /dev/null

# 5. Rapport Final
echo -e "\n5️⃣ Rapport de Diagnostic TWIST 09..."
curl -s -X GET "$BASE_URL/diagnostics/twist09" | json_pp

echo -e "\n🔍 DEBUG: Liste brute des connexions..."
curl -s -X GET "$BASE_URL/diagnostics/debug/connections" | json_pp

echo -e "\n✨ Test terminé."
