from  openjdk
ADD target/rest-demo.jar rest-demo.jar
ENTRYPOINT ["java","-jar","/rest-demo.jar"]