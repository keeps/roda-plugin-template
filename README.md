Example plugin template
-----------------------
This is a project template to create a plugin for [RODA](https://github.com/keeps/roda).
You can use it as a base for creating your own RODA plugin.

It includes:
* Source code example
* Unit testing bootstrap
* Install dependencies and plugin properties examples
* Build script to compile and create docker image with RODA and your plugin
* Quick run instructions
* README automatic generation instructions

## How to build and run

To build execute `./build.sh`, this will run with the latest RODA version.
If you require a different RODA version, e.g. vX.X.X, update the pom.xml parent version and execute `./build.sh vX.X.X`

The build script will compile the plugin and create a docker image with the base roda plus the plugin installed.
To run execute (roda-plugin-template should be replaced by the project folder name):

```shell
docker run -p 8080:8080 roda-plugin-template:latest
```

Then open in your favorite browser [http://localhost:8080](http://localhost:8080).

## How to auto-generate README.md

A new README.md file can be generated using the plugin description and parameters.

```shell
mvn compile exec:java -Droda.node.type=CONFIGS -Dmaven.test.skip -Denforcer.skip
cp target/README.md .
```

Here are the requirements to enable README.md generation:

* Depend on roda-core version >= `5.0.0` (make sure it does not have the scope defined as `provided`)
* Add plugin to exec a class with `RODA_PLUGIN` & maven target directory as parameters. E.g.:
```xml
<plugin>
   <groupId>org.codehaus.mojo</groupId>
   <artifactId>exec-maven-plugin</artifactId>
   <version>3.0.0</version>
   <configuration>
      <mainClass>org.roda.core.RodaCoreFactory</mainClass>
      <arguments>
         <argument>generatePluginsMarkdown</argument>
         <argument>${RODA_PLUGIN}</argument>
         <argument>[Production-ready]</argument> <!-- LIFE CYCLE STATUS -->
         <argument>${project.parent.version}</argument>
         <argument>${project.build.directory}</argument>
      </arguments>
   </configuration>
</plugin>
```
