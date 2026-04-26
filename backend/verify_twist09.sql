-- Vérifier les scores de réalité et les interactions
SELECT requester_id, receiver_id, reality_score, interaction_count, status 
FROM connections 
ORDER BY reality_score DESC;

-- Vérifier les présences confirmées
SELECT event_id, user_id, is_attended 
FROM event_registrations 
WHERE is_attended = true;

-- Vérifier les intérêts liés à un événement
SELECT user_id, tag, source_event_id 
FROM interests 
WHERE source_event_id IS NOT NULL;
