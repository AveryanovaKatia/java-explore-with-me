FROM amazoncorretto:21-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9098
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]