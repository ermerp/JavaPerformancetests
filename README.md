# Readme

## Gradle tasks

### build
```
./gradlew clean build
```

### run: mergesort
```
./gradlew :mergesort:run
```

## Bank: benchmark

### build Container
```
docker build -t bank-java .
```

### start DB
```
docker-compose up
docker-compose -f docker-compose_bank.yaml up
```

### reset DB
```
docker-compose down -v
docker-compose -f docker-compose_bank.yaml down 
```
