build:
    ./gradlew build jar

test:
    ./gradlew build jar
    cp ./build/libs/BTADiscordIntegration-1.0.0-7.3_03.jar run-server/mods/
    cd run-server && java -Xmx2G -jar fabric-server-launch.jar --nogui

run:
    cd run-server && java -Xmx2G -jar fabric-server-launch.jar --nogui