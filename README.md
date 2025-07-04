# FHIR Patient Data Pipeline Prototype

This is a prototype data pipeline using Spring Boot that receives Patient data in FHIR JSON format. The pipeline then transforms the received data into a simple tabular structure and then persists it to a database. A REST API endpoint accepts raw JSON in FHIR standard format for Patients and demonstrates basic data extraction and storage.

## Features

- POST endpoint for receiving FHIR Patient JSON ('/api/vi/patient')
- Parses the FHIR Patient resource using HAPI FHIR library (https://hapifhir.io/)
- Extracts some basic patient fields (patient ID, name, gender, birth date) and loads them into a relational database
- PostgreSQL database is loaded via Spring Data JPA

## Prerequisites

- Java 21+
- Maven 3.9.9+
- Docker and Docker Compose (optional for running PostgreSQL)

## Build

Clone repository</br>
build with Maven
``` bash
mvn clean package
```

Option 1: Use docker compose file to run PostgreSQL</br>

```bash
docker compose up -d
```

Option 2: Run your own instance of PostgreSQL</br>
Make sure to update application.properties file with your postgres connection details</br>

## Run the application
```bash
java -jar target/pipeline-prototype-1.0-SNAPSHOT.jar
```
The application starts on port 8080 by default.

## POST FHIR JSON

```bash
$ curl -i -X POST http://localhost:8080/api/v1/patient   -H "Content-Type: application/json"   -d '{
    "resourceType": "Patient",
    "id": "abc-123",
    "name": [{ "given": ["John"], "family": "Doe" }],
    "gender": "male",
    "birthDate": "1990-01-01"
  }'
```
If the post was successful a `201 created` will be returned and the extracted data will be stored in your running instance of postgres.

## Checking the Database
If you are running the postgres container using docker, you can check to see that the POST you made with cURL (or any other tool) has
loaded into the database

```bash
$ docker exec -it bcutts-pipeline-db-1 psql -U postgres
```
```sql
postgres=# SELECT * FROM patient_row;
```

## Testing
```bash
mvn test
```

# Future improvements
Support additional FHIR resource types (e.g., Observation, Encounter)

Enhance error handling and validation

Implement batch processing and Kafka integration for scalability

Add API authentication and security


