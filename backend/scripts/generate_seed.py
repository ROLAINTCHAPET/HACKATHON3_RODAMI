import csv
import json
import uuid
import datetime
import os
import re

DATA_DIR = "/home/tchoungs/Bureau/RODAMI/HACKATHON3_RODAMI/sujet9"
OUTPUT_SQL = "/home/tchoungs/Bureau/RODAMI/HACKATHON3_RODAMI/backend/scripts/seed_sujet9.sql"

def escape(s):
    if s is None: return "NULL"
    return "'" + str(s).replace("'", "''") + "'"

def generate_sql():
    sql_lines = [
        "-- Seed Data generated from sujet 9",
        "BEGIN;",
        "TRUNCATE users, profile_contexts, interests, associations, events, academic_schedules CASCADE;",
        "INSERT INTO users (id, nom, prenom, email, role) VALUES (1, 'Admin', 'CampusLink', 'admin@campuslink.fr', 'ADMIN') ON CONFLICT (id) DO NOTHING;"
    ]
    
    # 1. Students
    csv_path = os.path.join(DATA_DIR, "etudiants.csv")
    user_id_counter = 100
    
    with open(csv_path, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            uid = user_id_counter
            # Generate email and name based on student_id
            email = f"{row['student_id'].lower()}@univ.cm"
            nom = f"Nom_{row['student_id']}"
            prenom = f"Prenom_{row['student_id']}"
            
            sql_lines.append(f"INSERT INTO users (id, nom, prenom, email, role) VALUES ({uid}, {escape(nom)}, {escape(prenom)}, {escape(email)}, 'USER');")
            
            # Context
            # annee is like "L1", "M2" -> map to 1-5
            annee_map = {"L1": 1, "L2": 2, "L3": 3, "M1": 4, "M2": 5}
            annee_num = annee_map.get(row['annee'], 1)
            
            rythme = "STAGE" if row['navetteur'] == 'non' else "COURS" # Example mapping
            
            sql_lines.append(f"INSERT INTO profile_contexts (user_id, filiere, annee, semester, rythme, principal_location) VALUES ({uid}, {escape(row['filiere'])}, {annee_num}, 1, {escape(rythme)}, {escape(row['campus_site'])});")
            
            # Interests
            interests = row['interets'].split('|')
            for tag in interests:
                tag = tag.strip().capitalize()
                if tag:
                    sql_lines.append(f"INSERT INTO interests (user_id, tag) VALUES ({uid}, {escape(tag)}) ON CONFLICT DO NOTHING;")
            
            user_id_counter += 1

    # 2. Associations & Events
    json_path = os.path.join(DATA_DIR, "associations_evenements.json")
    with open(json_path, mode='r', encoding='utf-8') as f:
        data = json.load(f)
        
        # Categories mapping (from schema.sql)
        # Sport (1), Culture (2), Academique (3), Social (4), Informatique (5)
        cat_map = {"sport": 1, "culture": 2, "académique": 3, "social": 4, "informatique": 5}
        
        assoc_id_map = {}
        assoc_id_counter = 1
        for assoc in data['associations']:
            aid = assoc_id_counter
            assoc_id_map[assoc['id']] = aid
            sql_lines.append(f"INSERT INTO associations (id, nom, description, responsable_id, status) VALUES ({aid}, {escape(assoc['nom'])}, 1, 'APPROVED');")
            assoc_id_counter += 1
            
        for evt in data['evenements']:
            cat_id = 4 # default Social
            for key in cat_map:
                if key in evt['titre'].lower() or key in evt['tags'].lower():
                    cat_id = cat_map[key]
                    break
            
            start_dt = f"{evt['date']} {evt['heure_debut']}:00"
            
            sql_lines.append(f"INSERT INTO events (titre, description, date_debut, lieu, category_id, organisateur_id, association_id, status, max_participants) VALUES ({escape(evt['titre'])}, {escape(evt['description'])}, {escape(start_dt)}, {escape(evt['lieu'])}, {cat_id}, 1, {assoc_id_map.get(evt['organisateur_id'], 'NULL')}, 'PUBLISHED', {evt['places_max'] or 'NULL'});")

    # 3. ICS
    ics_path = os.path.join(DATA_DIR, "ETU0001.ics")
    if os.path.exists(ics_path):
        with open(ics_path, 'r') as f:
            content = f.read()
            events = re.findall(r"BEGIN:VEVENT.*?SUMMARY:(.*?)\n.*?DTSTART:(.*?)\n.*?DTEND:(.*?)\n.*?LOCATION:(.*?)\n.*?END:VEVENT", content, re.DOTALL)
            # Map to first student
            first_student_id = 100
            for summary, start, end, loc in events:
                # Format: 20260426T080000Z -> 2026-04-26 08:00:00
                s = start.strip()
                e = end.strip()
                s_fmt = f"{s[0:4]}-{s[4:6]}-{s[6:8]} {s[9:11]}:{s[11:13]}:{s[13:15]}"
                e_fmt = f"{e[0:4]}-{e[4:6]}-{e[6:8]} {e[9:11]}:{e[11:13]}:{e[13:15]}"
                sql_lines.append(f"INSERT INTO academic_schedules (user_id, summary, start_time, end_time, location) VALUES ({first_student_id}, {escape(summary.strip())}, {escape(s_fmt)}, {escape(e_fmt)}, {escape(loc.strip())});")

    sql_lines.append("COMMIT;")
    
    with open(OUTPUT_SQL, 'w', encoding='utf-8') as f:
        f.write("\n".join(sql_lines))
    
    print(f"✅ SQL script generated: {OUTPUT_SQL}")

if __name__ == "__main__":
    generate_sql()
