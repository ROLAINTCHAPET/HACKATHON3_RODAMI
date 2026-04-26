# 🚀 Guide de Test Swagger — CampusLink (Module 1 & 4)

Ce guide contient toutes les étapes pour tester les fonctionnalités des Modules 1 (Profil) et 4 (Gouvernance).

**URL Swagger :** `http://localhost:8080/swagger-ui.html`

---

## 🛡️ PARTIE A : PROFIL & UTILISATEUR (Module 1)

### 1. Inscription (Création de compte)
**Endpoint :** `POST /api/auth/register`
```json
{
  "nom": "Testeur",
  "prenom": "Etudiant",
  "email": "test.etudiant@campus.fr",
  "password": "password123"
}
```

### 2. Authentification (Bouton Authorize)
1. Clique sur **"Authorize"** en haut à droite.
2. Colle le `token` reçu lors de l'inscription.

### 3. Configuration Initiale (Setup)
**Endpoint :** `POST /api/profiles/setup`
```json
{
  "interests": ["Sport", "Tech"],
  "filiere": "Informatique",
  "annee": 2
}
```

### 4. Consultation & Mise à jour
- **Mon profil** : `GET /api/profiles/me`
- **Modifier** : `PUT /api/profiles/{id}` (ex: changer de filière)
- **Supprimer un intérêt** : `DELETE /api/profiles/{id}/interests/Sport`

---

## 🏛️ PARTIE B : GOUVERNANCE & BDE (Module 4)

### 5. Pilotage de l'Algorithme (RF-16/17)
**Endpoint :** `PUT /api/governance/rules/algo.highlight.category`
*Permet de changer la priorité actuelle du système.*
```json
{
  "value": "Culture"
}
```

### 6. Validation des Associations (RF-19)
**Endpoint :** `PUT /api/governance/associations/{id}/validate`
*Approuver une association pour lui donner des droits de publication.*
```json
{
  "status": "APPROVED"
}
```

### 7. Tableau de Bord d'Impact (RF-20)
**Endpoint :** `GET /api/governance/impact/events/{id}`
*Récupère le succès social d'un événement (nombre de liens créés).*

### 8. Traçabilité & Audit (RF-18)
**Endpoint :** `GET /api/governance/audit`
*Affiche l'historique de toutes les décisions administratives prises.*

---

## 💡 Astuces de test
- **Rôles** : Si tu veux tester la Gouvernance, assure-toi que ton utilisateur a le rôle `BDE` ou `ADMIN` dans la base de données.
- **Réinitialisation** : Si tu changes le schéma SQL, fais un `docker compose down -v` avant de relancer.
