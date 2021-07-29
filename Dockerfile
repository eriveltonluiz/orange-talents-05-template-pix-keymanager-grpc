FROM openjdk:11-jdk-slim
ARG JAR_FILE=build/libs/pix-grpc-0.1-all.jar
ADD ${JAR_FILE} pix-grpc.jar
ENTRYPOINT java -jar pix-grpc.jar