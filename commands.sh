git clone git@github.com:isel-leic-ls/2425-2-LEIC43D-G02.git ls_demonstration

cd ls_demonstration

git checkout tags/0.2.5

./gradlew clean build

POSTGRES_DB="false" ./gradlew runServer

POSTGRES_DB="true" DB_USER="postgres" DB_PASSWORD="postgres" ./gradlew runServer

