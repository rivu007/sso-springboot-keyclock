# SSO - Secure Spring Boot Apps/REST service with Keycloak

#### Technologies used in the project

* Gradle Wrapper
* Spring Boot
* Spring Security
* Spring Actuator
* Swagger
* Keycloak as authentication server
* MySQL (used by keycloak)
* Docker

#### Prerequisites installed

* JAVA 1.8
* Docker 17.09 or later
* Keycloak 3.4.3.Final
* MySQL

#### What is Keycloak?
Keycloak is an open source Identity and Access Management solution targeted towards modern applications and services.

Keycloak offers features such as Single-Sign-On (SSO), Identity Brokering and Social Login, User Federation, 
Client Adapters, an Admin Console, and an Account Management Console. To learn more about Keycloak, 
please visit the [official page](http://www.keycloak.org/documentation.html).

There’re several distributions to choose from. In this project, we are using Keycloak v3.4.3.Final.

To simplify the setup, we are using the docker image from [jboss official docker hub](https://hub.docker.com/r/jboss/keycloak/).


## Getting started

To start this web application just follow these steps:

* Checkout the project : `git clone https://github.com/rivu007/sso-springboot-keyclock.git`

* Navigate to the root of the project : `cd single-signon`

##### Setting Up a Keycloak Server

1. Run Keycloak server as docker-compose deamon mode : `docker-compose -f docker-compose/dev.yml up -d`

2. Navigate to the Keycloak admin console: `https://localhost:18443/auth`

3. Login using `admin/password`

4. Importing the realm:
To configure the initial Keycloak Realm, you just import it through the Keycloak Web Console. Import the [keycloak-sso.json](https://www.secrz.de/bitbucket/projects/OTC/repos/single-signon/browse/src/main/resources/keycloak-sso.json) which is located at the `resource` directory of this repo.

Once the import is done. You should see a new realm named: `sso`. That's it!

##### Setting up the SSO REST service

5. It's time to run the SSO REST service by issuing: 

```
$ ./gradlew bootRun
``` 

To do a quick sanity check, please browse to : `http://localhost:8080/health`

Expected Response:
```
{
    "status": "UP"
}
```

## Running the Test

* Simply run the `test` gradle task to have all the test running:

```
$ gradlew test
```

## Running end2end : REST service with keycloak

1. First you need to build the docker image. Run `gradle clean build docker` to build the docker image. To verify the creating of the image 
   issue `docker images | grep single-signon`
2. Run `docker-compose -f docker-compose/prod.yml up -d`


## REST API documentation

Please browse to below url for REST api docs:

```
http://localhost:8080/swagger-ui.html
```

## Lombok Setup:

* IntelliJ

  * Go to `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processing`
  * Check `Enable Annotation processing`

* Eclipse

  * Change to directory which contains lombok.jar (if not available download it)
  * Run `java -jar lombok.jar` in console to open Lombok Installation Wizard
  * Define path to eclipse.exe
  * Restart eclipse

## Resources

* [Keycloak website](http://www.keycloak.org/)
* [Spring Boot Keycloak Adapter Doc](http://www.keycloak.org/docs/3.3/securing_apps/topics/oidc/java/spring-boot-adapter.html)
* [Secure your Spring Boot applications with Keycloak](https://developers.redhat.com/blog/2017/05/25/easily-secure-your-spring-boot-applications-with-keycloak/)
* [Keycloak quickstarts](https://github.com/keycloak/keycloak-quickstarts)
