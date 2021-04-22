FROM openjdk:11
WORKDIR /app
COPY src/ .
COPY invoice.txt .
RUN javac *.java
CMD java Main