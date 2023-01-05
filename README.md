# EVAM MARKETING COMMUNICATION INTEGRATION
The custom communication module is a spring boot project (consume topic: customcomTopic).

**Default Port:** 9999  
**Default Context Path:** /communication-integration

**Health Endpoint:** /communication-integration/health

## REQUIREMENTS
```
OpenJDK 8
Lombok IntelliJ plugin 
IntelliJ IDEA (Compiler > Annotation Processors > Enable annotation processing active for lombok)
```
## BUILD
```
./mvnw clean package
```
Output: marketing-communication-integration-**version**.zip in target folder
## HOW TO RUN
```
java -jar marketing-communication-integration-__VERSION__.jar
```
### Wrapper (Recommended)
```
chmod +x bin/*
```
#### Start
```
./bin/marketing-communication-integration start
```
#### Stop
```
./bin/marketing-communication-integration stop
```
#### Status
```
./bin/marketing-communication-integration status
```