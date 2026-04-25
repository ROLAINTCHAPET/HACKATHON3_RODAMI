# Module Gouvernance / Administration BDE
> ⚠️ **Ce module est pris en charge par une autre équipe.**

## Responsabilités
- Définition et gestion des règles de gouvernance (règles figées Admin + règles ajustables BDE)
- Panneau d'administration BDE (priorités catégories événements, fréquence push)
- Traçabilité et `audit_logs`
- Validation des associations organisatrices

## Package
`com.rodami.campuslink.modules.governance`

## Entités DB
- `governance_rules` — règles avec flag `is_fixed`
- `audit_logs` — journal d'actions (acteur, action, entité, détails JSONB)

## API prévue
```
GET  /api/governance/rules           → liste des règles
PUT  /api/governance/rules/{key}     → modifier une règle (ADMIN only pour fixed=true)
GET  /api/governance/audit-logs      → journal d'audit
POST /api/governance/associations/validate → valider une association
```

## Points d'intégration avec les modules RODAMI
- `events` : utilise `governance_rules.push.enabled` et les priorités de catégorie
- `matching` : utilise `governance_rules.cold_start.enabled`
