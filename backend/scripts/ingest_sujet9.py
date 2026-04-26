import csv
import json
import psycopg2
import uuid
import datetime
import os
import re

# Configuration
DB_CONFIG = {
    "host": "localhost",
    "database": "campuslink_db",
    "user": "campuslink",
    "password": "campuslink_pass",
    "port": 5432
}

DATA_DIR = "/home/tchoungs/Bureau/RODAMI/HACKATHON3_RODAMI/sujet9"

def connect_db():
    return psycopg2.connect(**DB_CONFIG)

def ingest_students(conn):
    print("--- Ingestion des étudiants ---")
    cur = conn.cursor()
    csv_path = os.path.join(DATA_DIR, "etudiants.csv")
    
    with open(csv_path, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        count = 0
        for row in reader:
            # Insert User
            cur.execute("""
                INSERT INTO users (nom, prenom, email, role)
                VALUES (%s, %s, %s, 'USER')
                ON CONFLICT (email) DO UPDATE SET nom = EXCLUDED.nom
                RETURNING id
            """, (row['nom'], row['prenom'], row['email']))
            user_id = cur.fetchone()[0]
            
            # Insert Profile Context
            cur.execute("""
                INSERT INTO profile_contexts (user_id, filiere, annee, semester, rythme)
                VALUES (%s, %s, %s, %s, %s)
                ON CONFLICT (user_id) DO NOTHING
            """, (user_id, row['filiere'], row['annee'], row['semestre'], row.get('rythme', 'COURS')))
            
            # Insert Interests
            interests = row['interets'].split('|')
            for tag in interests:
                tag = tag.strip().capitalize()
                if tag:
                    cur.execute("""
                        INSERT INTO interests (user_id, tag)
                        VALUES (%s, %s)
                        ON CONFLICT DO NOTHING
                    """, (user_id, tag))
            count += 1
    
    conn.commit()
    print(f"✅ {count} étudiants importés.")

def ingest_associations_and_events(conn):
    print("--- Ingestion des associations et événements ---")
    cur = conn.cursor()
    json_path = os.path.join(DATA_DIR, "associations_evenements.json")
    
    with open(json_path, mode='r', encoding='utf-8') as f:
        data = json.load(f)
        
        # 1. Map Categories to IDs
        cur.execute("SELECT id, nom FROM event_categories")
        cat_map = {row[1]: row[0] for row in cur.fetchall()}
        
        # 2. Associations
        assoc_id_map = {}
        for assoc in data['associations']:
            cur.execute("""
                INSERT INTO associations (nom, description, responsable_id, status)
                VALUES (%s, %s, 1, 'APPROVED')
                ON CONFLICT DO NOTHING
                RETURNING id
            """, (assoc['nom'], f"Association cible: {assoc['filiere_cible']}"))
            res = cur.fetchone()
            if res:
                assoc_id_map[assoc['id']] = res[0]
            else:
                # Get existing
                cur.execute("SELECT id FROM associations WHERE nom = %s", (assoc['nom'],))
                assoc_id_map[assoc['id']] = cur.fetchone()[0]
        
        # 3. Events
        count = 0
        for evt in data['evenements']:
            # Find category
            tags = evt['tags'].split('|')
            main_tag = tags[0].capitalize()
            # Try to match tag with category
            cat_id = cat_map.get('Social') # fallback
            for cat_name in cat_map:
                if cat_name.lower() in evt['titre'].lower() or cat_name.lower() in evt['tags'].lower():
                    cat_id = cat_map[cat_name]
                    break
            
            # Parse Date
            start_dt = datetime.datetime.strptime(f"{evt['date']} {evt['heure_debut']}", "%Y-%m-%d %H:%M")
            end_dt = start_dt + datetime.timedelta(hours=evt['duree_heures'])
            
            cur.execute("""
                INSERT INTO events (titre, description, date_debut, date_fin, lieu, category_id, organisateur_id, association_id, status, max_participants)
                VALUES (%s, %s, %s, %s, %s, %s, 1, %s, 'PUBLISHED', %s)
            """, (evt['titre'], evt['description'], start_dt, end_dt, evt['lieu'], cat_id, assoc_id_map.get(evt['organisateur_id']), evt['places_max']))
            count += 1
            
    conn.commit()
    print(f"✅ {len(data['associations'])} associations et {count} événements importés.")

def ingest_ics(conn):
    print("--- Ingestion des calendriers (ICS) ---")
    cur = conn.cursor()
    # On va mapper ETU0001.ics au premier utilisateur importé pour le test
    cur.execute("SELECT id FROM users WHERE email LIKE 'student%' OR role = 'USER' LIMIT 1")
    user_id = cur.fetchone()[0]
    
    ics_path = os.path.join(DATA_DIR, "ETU0001.ics")
    if not os.path.exists(ics_path):
        print("⚠️ Fichier ICS non trouvé.")
        return

    with open(ics_path, 'r') as f:
        content = f.read()
        # Simple regex parser for ICS (no lib needed)
        events = re.findall(r"BEGIN:VEVENT.*?SUMMARY:(.*?)\n.*?DTSTART:(.*?)\n.*?DTEND:(.*?)\n.*?LOCATION:(.*?)\n.*?END:VEVENT", content, re.DOTALL)
        
        for summary, start, end, loc in events:
            # DTSTART:20260426T080000Z
            s_dt = datetime.datetime.strptime(start.strip(), "%Y%m%dT%H%M%SZ")
            e_dt = datetime.datetime.strptime(end.strip(), "%Y%m%dT%H%M%SZ")
            
            cur.execute("""
                INSERT INTO academic_schedules (user_id, summary, start_time, end_time, location)
                VALUES (%s, %s, %s, %s, %s)
            """, (user_id, summary.strip(), s_dt, e_dt, loc.strip()))
            
    conn.commit()
    print(f"✅ Calendrier pour l'utilisateur {user_id} importé.")

if __name__ == "__main__":
    try:
        conn = connect_db()
        ingest_students(conn)
        ingest_associations_and_events(conn)
        ingest_ics(conn)
        conn.close()
        print("\n🚀 Pipeline de données terminé avec succès !")
    except Exception as e:
        print(f"❌ Erreur lors de l'ingestion : {e}")
