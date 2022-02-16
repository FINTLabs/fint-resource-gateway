# FINT Resource Gateway
FINT Resource Gateway has the responsibility to the resources from FINT and put them on the event hub

## Run Kafka local
`docker-compose up -d`

## Connect to Aiven

### Get certificates for Aiven
`avn service user-kafka-java-creds <Aiven kafka cluster> --username <Aiven service user> -d ./ --password <something top secret>`

### Properties
```
spring.security.oauth2.client.registration.fint.client-id=
spring.security.oauth2.client.registration.fint.client-secret=
fint.client.username=
fint.client.password=
spring.kafka.bootstrap-servers=<Url for the Aiven cluster>
spring.kafka.ssl.protocol=SSL
spring.kafka.ssl.key-password=<password from the avn service user-kafka-java-creds command>
spring.kafka.ssl.key-store-location=client.keystore.p12
spring.kafka.ssl.key-store-password=<password from the avn service user-kafka-java-creds command>
spring.kafka.ssl.key-store-type=PKCS12
spring.kafka.ssl.trust-store-location=client.truststore.jks
spring.kafka.ssl.trust-store-password=<password from the avn service user-kafka-java-creds command>
spring.kafka.ssl.trust-store-type=JKS
```
