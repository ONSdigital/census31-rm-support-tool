FROM eclipse-temurin:21-jre-alpine

CMD ["java", "-jar", "/opt/census-rm-support-tool.jar"]

# Create a system group and user without forcing UID/GID
RUN addgroup --system supporttool && \
    adduser --system --ingroup supporttool supporttool

USER supporttool

COPY target/census-rm-support-tool*.jar /opt/census-rm-support-tool.jar
