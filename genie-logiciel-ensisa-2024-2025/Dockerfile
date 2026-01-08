FROM maven:3-eclipse-temurin-17-alpine AS build
ENV MAVEN_CLI_OPTS="--batch-mode -Dmaven.repo.local=libs"
ENV MAVEN_CLI_DISABLE="-DskipTests -Dskip.tests=true -Dskip.integration=true -Dskip.spotbugs=true \
                       -Dskip.jacoco.report=true -Dskip.jacoco.check=true -Dskip.jacoco.aggregate=true \
                       -Dskip.pitest.aggregate=true -Dskip.pitest.mutation=true"
WORKDIR /usr/src
#RUN grep -r '^\s*skip-networking' /etc/my.cnf.d/ | cut -d: -f1 | xargs -I CNF sed -i 's/^skip-networking/#skip-networking/' CNF
RUN mkdir -p $HOME/.m2 ; echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd"><mirrors><mirror><id>ensisa</id><name>Nexus ENSISA</name><url>https://nexus.cluster.ensisa.uha.fr/repository/maven-public/</url><mirrorOf>*</mirrorOf>*</mirror></mirrors><localRepository>/usr/share/maven/ref/repository</localRepository></settings>' > $HOME/.m2/settings.xml
# Copier les pom dans le build
COPY pom.xml /usr/src/
COPY mantest-aggregate/pom.xml /usr/src/mantest-aggregate/
COPY mantest-dao-mem/pom.xml /usr/src/mantest-dao-mem/
COPY mantest-model/pom.xml /usr/src/mantest-model/
COPY mantest-app/pom.xml /usr/src/mantest-app/
# Cache à dépendances du projet
COPY libs/ /usr/src/libs/
# Recuperer les dependances manquantes
RUN mvn $MAVEN_CLI_OPTS dependency:go-offline
# Copier les sources
COPY mantest-dao-mem/ /usr/src/mantest-dao-mem/
COPY mantest-model/ /usr/src/mantest-model/
COPY mantest-app/ /usr/src/mantest-app/
# Build le war de l'application
RUN mvn $MAVEN_CLI_OPTS install $MAVEN_CLI_DISABLE

FROM tomcat:11-jre17-temurin
LABEL authors="ludwig_van_schlett"

RUN rm -rf ${CATALINA_HOME}/webapps/*

COPY --from=build /usr/src/mantest-app/target/mantest-app.war ${CATALINA_HOME}/webapps/ROOT.war

HEALTHCHECK --interval=10s --timeout=2s --start-period=10s --retries=3 \
    CMD curl --fail http://localhost:8080/ 2>/dev/null
