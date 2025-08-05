FROM openjdk:23
WORKDIR /usr/app
COPY ./static-content ./static-content
COPY ./build/libs ./libs

# Construct classpath by including all jars in /usr/app/libs
CMD ["sh", "-c", "java -cp './libs/*' pt.isel.ls.AppKt"]