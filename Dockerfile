FROM ubuntu:16.04
MAINTAINER Prathik Raj "prathik011@gmail.com"

RUN apt-get update
RUN apt-get install -y default-jdk
RUN apt-get install -y maven
RUN apt-get install -y curl
RUN curl -o jetty.tar.gz http://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.6.v20170531/jetty-distribution-9.4.6.v20170531.tar.gz
RUN tar xvzf jetty.tar.gz
RUN mkdir service
RUN mkdir service/api
RUN mkdir service/sbs
ADD sbs service/sbs
ADD api service/api
COPY pom.xml service
RUN cd service && mvn clean && mvn install && cd api && mvn package
RUN cp /service/api/target/api-1.0-SNAPSHOT.war /jetty-distribution-9.4.6.v20170531/webapps
RUN apt-get install -y python-pip
RUN pip install supervisor
COPY supervisord.conf /etc/supervisor/supervisord.conf
RUN mkdir /var/log/supervisor/
CMD ["supervisord", "-c", "/etc/supervisor/supervisord.conf"]
