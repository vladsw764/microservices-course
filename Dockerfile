# Use the official Jenkins image as a starting point
FROM jenkins/jenkins:latest-jdk17

# Maintainer Info
LABEL maintainer="your-email@example.com"

# Switch to root user to install Maven
USER root

# Install Maven
RUN apt-get update && apt-get install -y maven

# Switch back to the Jenkins user
USER jenkins

# Set the environment variable for Maven
ENV MAVEN_HOME /usr/share/maven
ENV PATH $MAVEN_HOME/bin:$PATH

# Copy Jenkins job configurations and other setup configurations if needed
# COPY jobs/ /var/jenkins_home/jobs/

# Expose the default Jenkins port
EXPOSE 8080