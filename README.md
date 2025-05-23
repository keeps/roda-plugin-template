# Example plugin template

This is a project template to create a plugin for [RODA](https://github.com/keeps/roda).
You can use it as a base for creating your own RODA plugin.

It includes:

- Source code example
- Unit testing bootstrap
- Install dependencies and plugin properties examples
- Build script to compile and create docker image with RODA and your plugin
- Quick run instructions
- README automatic generation instructions

## How to build and run

To build execute `./build.sh`, this will run with the latest RODA version.

If you require a different RODA version, e.g. vX.X.X, update the `pom.xml` parent version and execute `./build.sh vX.X.X`

The build script will compile the plugin and create a docker image with the RODA with the plugin installed.

To run execute:

```shell
./deploy.sh [clean, down, pull, up]
```

Then open in your favourite browser [http://localhost:8080](http://localhost:8080).

## How to run locally

Clone [RODA](https://github.com/keeps/roda) repository and execute the following commands:

```shell
sudo apt install clamdscan
echo "TCPSocket 3310
TCPAddr localhost" | sudo tee /etc/clamav/clamd.conf
```

To run locally:

```shell
cd path/to/where/roda/was/cloned
mvn -pl roda-ui/roda-wui -am gwt:compile -Pdebug-main -Dscope.gwt-dev=compile

./roda-ui/roda-wui/copy_gwt_rpc.sh

mvn install -Pcore -DskipTests

mkdir -p $HOME/.roda/data/storage
docker compose -f deploys/standalone/docker-compose-dev.yaml up -d

# Open WUI in Spring boot
mvn -pl roda-ui/roda-wui -am spring-boot:run -Pdebug-mai

# Open codeserver
mvn -f dev/codeserver gwt:codeserver -DrodaPath=$(pwd)
```

More information in RODA [DEV_NOTES](https://github.com/keeps/roda/blob/master/DEV_NOTES.md#dev-notes) file.

To install the plugin locally you need to:

```shell
cd /path/to/plugin-template
mvn clean package
mkdir -p $HOME.roda/config/plugins
cp -r target/<roda-plugin-template> $HOME.roda/config/plugins

# Optional step is you need to install dependencies
./$HOME.roda/config/plugins/<roda-plugin-template>/install_dependencies.sh
```

> :warning: This step should be repeated every time you change plugin's source code.

Enable running non-signed plugins:

```shell
cd /path/to/plugin-template
cp deploys/roda-core.properties $HOME.roda/

# Restart RODA
```

> :blue_book: Check the documentation [on how to sign your own plugins](https://github.com/keeps/roda/blob/master/documentation/Plugin_signing.md).
