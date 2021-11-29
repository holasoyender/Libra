FROM azul/zulu-openjdk:17

WORKDIR /Libra

COPY build/libs/Libra-1.0-all.jar Libra.jar

CMD ["java", "-jar", "Libra.jar"]