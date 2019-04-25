############################
### Base for build image ###
############################
FROM maven:latest AS build

MAINTAINER Felix Klauke <info@felix-klauke.de>

######################
### Copy all files ###
######################
COPY . .

################
### Build it ###
################
RUN mvn clean install package

########################
### Base for runtime ###
########################
FROM openjdk:8 AS runtime

###############
### Workdir ###
###############
WORKDIR /opt/app

###################
### Environment ###
###################
ENV HEIMDALL_SERVER_HOST=0.0.0.0
ENV HEIMDALL_SERVER_PORT=8080

###################
### Copy server ###
###################
COPY --from=build server/target/heimdall-server.jar /opt/app/server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "server.jar" ]

