FROM jboss/wildfly:18.0.0.Final

RUN mkdir /opt/jboss/wildfly/modules/system/layers/base/com/mariadb
RUN mkdir /opt/jboss/wildfly/modules/system/layers/base/com/mariadb/main
COPY ./config/mariadb/main/module.xml /opt/jboss/wildfly/modules/system/layers/base/com/mariadb/main
COPY ./config/mariadb/main/*.jar /opt/jboss/wildfly/modules/system/layers/base/com/mariadb/main
COPY ./config/standalone.xml /opt/jboss/wildfly/standalone/configuration/
COPY ./target/*.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080
EXPOSE 9990

RUN /opt/jboss/wildfly/bin/add-user.sh swahl Stage126$% --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]