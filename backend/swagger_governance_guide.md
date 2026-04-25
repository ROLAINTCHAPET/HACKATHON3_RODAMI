# 🛡️ Guide de Test Swagger — Module 4 (Gouvernance)

Ce guide permet de tester les fonctions d'administration et de pilotage du BDE.

**Note :** Pour ces tests, tu dois utiliser un compte ayant le rôle `BDE` ou `ADMIN`.

---

## 1. Gestion des Règles Algorithmiques (RF-16/17)

### ✅ Modifier une règle ajustable (BDE)
**Endpoint :** `PUT /api/governance/rules/algo.highlight.category`
**JSON :**
```json
{
  "value": "Culture"
}
```
*Résultat attendu : 200 OK. L'IA mettra désormais en avant la Culture.*

### ❌ Tenter de modifier une règle figée (BDE)
**Endpoint :** `PUT /api/governance/rules/matching.max_suggestions`
**JSON :**
```json
{
  "value": "50"
}
```
*Résultat attendu : 403 Forbidden ou Erreur. Le système bloque car seul l'Admin peut toucher à ce réglage.*

---

## 2. Validation des Associations (RF-19)

### ✅ Approuver une association
**Endpoint :** `PUT /api/governance/associations/{id}/validate`
**JSON :**
```json
{
  "status": "APPROVED"
}
```
*Note : Remplace {id} par l'ID d'une association en attente.*

---

## 3. Tableau de Bord d'Impact (RF-20)

### ✅ Consulter l'impact d'un événement
**Endpoint :** `GET /api/governance/impact/events/{id}`

*Résultat attendu : Tu recevras un JSON avec le nombre de participants, le nombre de connexions créées et le taux de succès (%).*

---

## 4. Consultation de l'Audit (RF-18)

### ✅ Voir les traces de décision
**Endpoint :** `GET /api/governance/audit`

*Note : Permet de voir l'historique des changements de règles et des validations pour assurer la traçabilité totale.*
