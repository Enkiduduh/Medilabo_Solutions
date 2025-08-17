# MediLabo Solutions 

Plateforme **microservices** pour la gestion de patients (SQL), de leurs **notes** (MongoDB/NoSQL) et un service **Prévoyance** qui calcule un **niveau de risque** (à partir des notes + âge + genre). Un **Gateway** unifie l’accès aux APIs et un **front React** consomme le tout.  
Les **sessions** sont **partagées** via **Spring Session Redis** avec le cookie **`JSESSIONID`**.

---

## 📦 Architecture

```
MediLaboSolutions/
├─ backend/
│  ├─ microservice_patient/     # Spring Boot (JPA + MySQL) + Auth (login/logout/me)
│  ├─ microserviceNote/         # Spring Boot (MongoDB) - CRUD Notes + recherche
│  ├─ gateway/                  # Spring Cloud Gateway MVC (port 8081)
│  └─ microservicePrevoyance/   # Agrégateur (Feign) - calcul du risque
├─ frontend/
│  └─ microservice_interface/   # React (Vite, port 5173)
└─ docker-compose.yml           # Redis + MongoDB pour le dev local
```

### Ports par défaut
- **Gateway**: `8081`
- **Patient**: `8080`
- **Notes**: `8082`
- **Prévoyance**: `8083`
- **Front**: `5173`
- **Redis**: `6379`
- **MongoDB**: `27017`
- **MySQL** (local): `3306`

---

## 🚀 Démarrage rapide (Dev)

### 0) Prérequis
- Java **21**, Maven
- Node.js **18+**
- Docker Desktop (ou équivalent)
- MySQL local (pour Patients) — ou un conteneur si vous préférez

### 1) Lancer Redis + MongoDB
```bash
docker compose up -d
# services attendus : medilabo-redis, medilabo-mongo
```

### 2) Démarrer les microservices (dans chaque dossier)
```bash
# Exemple
cd backend/microservice_patient && mvn spring-boot:run
cd backend/microserviceNote && mvn spring-boot:run
cd backend/microservicePrevoyance && mvn spring-boot:run
cd backend/gateway && mvn spring-boot:run
```

### 3) Frontend
```bash
cd frontend/microservice_interface
npm install
npm run dev
# http://localhost:5173
```

> ⚠️ **Important — sessions partagées** : assurez-vous que **tous** les services utilisent
> le **même cookie** (`JSESSIONID`) et le **même namespace** Redis (`medilabo:sessions`).
> Voir la section *Configuration essentielle* ci-dessous.

---

## ⚙️ Configuration essentielle

### Spring Session Redis (dans **patient**, **notes**, **prevoyance**)
`application.yml` minimal :
```yaml
server:
  servlet:
    session:
      cookie:
        name: JSESSIONID
        same-site: Lax
        http-only: true

spring:
  session:
    store-type: redis
    redis:
      namespace: medilabo:sessions
      flush-mode: on_save
  data:
    redis:
      host: localhost
      port: 6379
```


### Gateway (8081) — routes typiques
`backend/gateway/src/main/resources/application.yml` :
```yaml
spring:
  cloud:
    gateway:
      server:
        webmvc:
          routes:
            - id: patient-login
              uri: http://localhost:8080
              predicates: [ Path=/api/connexion ]
            - id: patient-logout
              uri: http://localhost:8080
              predicates: [ Path=/api/logout ]
            - id: patient-me
              uri: http://localhost:8080
              predicates: [ Path=/api/me ]
            - id: patient-crud
              uri: http://localhost:8080
              predicates: [ Path=/api/patients/** ]

            - id: notes
              uri: http://localhost:8082
              predicates: [ Path=/api/notes/** ]

            - id: prevoyance
              uri: http://localhost:8083
              predicates: [ Path=/api/prevoyance/** ]
```


### Patient (MySQL)
`backend/microservice_patient/src/main/resources/application.yml` :
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medilabo?useSSL=false&serverTimezone=UTC
    username: medilabo
    password: medilabo
  jpa:
    hibernate.ddl-auto: update
    open-in-view: false
```

### Notes (MongoDB)
`backend/microserviceNote/src/main/resources/application.yml` :
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/medilabo
```

Importer des notes d’exemple :
```bash
docker cp notes.json medilabo-mongo:/tmp/notes.json
docker exec medilabo-mongo mongoimport   --db medilabo --collection notes --file /tmp/notes.json --jsonArray
```

---

## 🔐 Authentification (via **microservice_patient**)

- **Login** : `POST /api/connexion` (form-urlencoded)
  - Body : `username=...&password=...`
  - 200 → `{"message":"Login successful","user":"..."}`
- **Logout** : `POST /api/logout`
- **Session** : `GET /api/me` → 200 si connecté, **401** sinon

Exemple via **gateway** :
```bash
curl -c cookie.txt -i -X POST http://localhost:8081/api/connexion   -H "Content-Type: application/x-www-form-urlencoded"   -d "username=James&password=password"
```

---

## 📚 API (via Gateway)

### Patients → 8081 ➜ 8080
- `GET /api/patients/{id}` — détails patient (incl. `genre: "H"|"F"`, date de naissance)
- `GET /api/me` — check session

### Notes → 8081 ➜ 8082
- `GET /api/notes/{patientId}?page=0&size=20` — liste paginée (tri `createdAt` desc)
- `POST /api/notes` — créer
  ```json
  { "patientId": 4, "content": "Texte de la note
sur plusieurs lignes" }
  ```
- **Recherche** (texte intégral) :  
  `GET /api/notes/search?patientId=4&q=douleur%20thoracique&mode=all&page=0&size=20`  
  - `mode=any` (par défaut) : OR ; `mode=all` : tous les mots

### Prévoyance → 8081 ➜ 8083
- **Risque** : `GET /api/prevoyance/patient/{id}/risk`
  ```json
  {
    "patientId": 4,
    "age": 28,
    "genre": "H",
    "matchedCount": 3,
    "code": "IN_DANGER",
    "labelFr": "Danger",
    "matchedKeywords": ["toux","fievre","poids"]
  }
  ```

#### Règles de classification
- **NONE** (Aucun risque) : 0 déclencheur
- **BORDERLINE** (Risque limité) : **âge > 30** et **2..5** déclencheurs
- **IN_DANGER** (Danger) :
  - **âge ≤ 30 & H** : ≥ 3 déclencheurs
  - **âge ≤ 30 & F** : ≥ 4 déclencheurs
  - **âge > 30** : 6 ou 7 déclencheurs
- **EARLY_ONSET** (Apparition précoce) :
  - **âge ≤ 30 & H** : ≥ 5 déclencheurs
  - **âge ≤ 30 & F** : ≥ 7 déclencheurs
  - **âge > 30** : ≥ 8 déclencheurs

> Les **déclencheurs** sont configurables dans `microservicePrevoyance` (`prevoyance.keywords`).  
> Détection **insensible à la casse**, **sans accents**, sur **mots entiers**.

---

## 🖥️ Intégration Front (React)

- Requêtes via **Gateway** (`/api/...`) avec cookies :
  ```js
  fetch('/api/...', { credentials: 'include' })
  ```
- **Login** recommandé en `application/x-www-form-urlencoded` :
  ```js
  const body = new URLSearchParams({ username, password });
  await fetch('/api/connexion', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    credentials: 'include',
    body
  });
  ```
- **Garde de route** : `GET /api/me` → si **401** rediriger vers `/connexion`.
- **Affichage des notes** : conserver les sauts de ligne avec CSS
  ```css
  .note-content { white-space: pre-wrap; word-break: break-word; }
  ```
- **Badge risque** : utilisez `code` (`NONE | BORDERLINE | IN_DANGER | EARLY_ONSET`).

---

## 🧪 Exemples `curl`

```bash
# 1) Login
curl -c cookie.txt -i -X POST http://localhost:8081/api/connexion   -H "Content-Type: application/x-www-form-urlencoded"   -d "username=James&password=password"

# 2) Lister notes
curl -b cookie.txt "http://localhost:8081/api/notes/4?page=0&size=20"

# 3) Créer une note
curl -b cookie.txt -i -X POST http://localhost:8081/api/notes   -H "Content-Type: application/json"   --data-binary $'{"patientId":4,"content":"Ligne 1
Ligne 2"}'

# 4) Calcul du risque
curl -b cookie.txt "http://localhost:8081/api/prevoyance/patient/4/risk"
```


---

## 🧩 Stack

- **Spring Boot 3.5**, **Spring Security 6**, **Spring Session Redis**
- **Spring Cloud 2025** (Gateway MVC, OpenFeign, Resilience4j)
- **MySQL** (patients), **MongoDB** (notes)
- **React (Vite)**

---

## 📄 Licence

Projet pédagogique
