FROM maven:alpine AS maven_builder

WORKDIR /maven
COPY pom.xml .
RUN mvn dependency:copy-dependencies

FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0 as sbt_builder

COPY build.sbt /sbt/build.sbt
ADD src /sbt/src/
ADD project /sbt/project/
WORKDIR /sbt
RUN sbt assembly

# FROM apache/spark:v3.1.3
FROM spark:3.5.1

COPY --from=maven_builder maven/target/dependency/ /opt/spark/jars/
COPY --from=sbt_builder sbt/target/scala-2.12/streamprocessor-assembly-1.0.jar /opt/spark/jars/

USER root
RUN chmod 644 /opt/spark/jars/streamprocessor-assembly-1.0.jar
RUN chown 185:185 /opt/spark/jars/streamprocessor-assembly-1.0.jar