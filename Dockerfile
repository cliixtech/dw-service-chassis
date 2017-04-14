FROM java:openjdk-8-jre

RUN mkdir -p /opt/application/
COPY app_config.yml /opt/application
COPY build/distributions/application.tar /opt/

RUN cd /opt && tar xf application.tar
RUN chown -R daemon /opt/application

EXPOSE 8080
USER daemon
WORKDIR /opt/application
CMD ["bin/application", "server", "app_config.yml"]
