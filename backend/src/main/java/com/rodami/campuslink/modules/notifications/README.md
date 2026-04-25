# Module Notifications
> ⚠️ **Ce module est pris en charge par une autre équipe.**

## Responsabilités
- Distribution proactive des notifications (flux PUSH algorithmique)
- Notifications en temps réel (WebSocket réactif ou SSE)
- Marquage lu/non-lu, préférences utilisateur
- Intégration avec le flux PUSH du module Matching

## Package
`com.rodami.campuslink.modules.notifications`

## Entités DB
- `notifications` — (user_id, type, title, body, is_read, metadata JSONB)

## API prévue
```
GET  /api/notifications              → liste des notifications de l'utilisateur connecté
POST /api/notifications/{id}/read   → marquer comme lu
DELETE /api/notifications/{id}      → supprimer
GET  /api/notifications/stream      → flux SSE temps réel (Flux<ServerSentEvent>)
```

## Points d'intégration avec les modules RODAMI
- `matching.PushDiscoveryService` → publie des événements de recommandation → notifications
- `events.EventDiffusionService` → publie des notifications d'événements selon les règles BDE
