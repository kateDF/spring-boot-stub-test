# Spring boot application stub testing
## __Execution__

#### Run application
_(MongoDB configuration is in application-prod.properties file. By default should be MongoDB connect to localhost:27017. Database name: db-karpuk)_
```
mvn spring-boot:run -Dspring.profiles.active=prod
```
#### Run tests
1. With MongoDB emulator
```
mvn clean test
```
2. With real MongoDB

_(MongoDB configuration is in application.properties file. By default should be MongoDB connect to localhost:27017. Database name: db-karpuk)_
```
mvn clean test -P!mongo-emulator
```
## __Useful links:__
* [Web Layer tests](https://spring.io/guides/gs/testing-web/)
* [Exchange rates REST api](https://exchangeratesapi.io)
