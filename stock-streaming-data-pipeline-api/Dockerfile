FROM openjdk:21

ENV DB_NAME=gonadmin
ENV DB_PASSWORD=password123!
ENV DB_URI=jdbc:postgresql://172.30.1.206:5432/gon_stock_dashboard
ENV CASSANDRA_HOST=172.30.1.203
ENV CASSANDRA_KEYSPACE=market
ENV CASSANDRA_USERNAME=cassandra
ENV CASSANDRA_PASSWORD=cassandra

COPY build/libs/stock-streaming-data-pipeline-api-0.0.1-SNAPSHOT.jar /

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "-jar", "/stock-streaming-data-pipeline-api-0.0.1-SNAPSHOT.jar"]