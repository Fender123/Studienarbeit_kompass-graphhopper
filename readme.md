# Kompass GraphHopper with edge to OSM-ID mapping

## Build jar
`mvn clean compile assembly:single install`

`install` adds the package to local maven repository so it can be use by other projects

like `mvn package` but includes all dependencies. Resulting jar is located in `target` dir, named like `kompass-graphhopper-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

## Call

`java -Xmx4000m -Xms4000m -jar target/kompass-graphhopper-0.0.1-SNAPSHOT-jar-with-dependencies.jar config=config.properties graph.location=europe_alps-gh osmreader.osm=europe_alps.pbf`