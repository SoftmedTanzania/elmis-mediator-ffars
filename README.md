# Tanzania FFARS - eLMIS Mediator
[![Java CI Badge](https://github.com/SoftmedTanzania/elmis-mediator-ffars/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/SoftmedTanzania/elmis-mediator-ffars/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Coverage Status](https://coveralls.io/repos/github/SoftmedTanzania/elmis-mediator-ffars/badge.svg?branch=development)](https://coveralls.io/github/SoftmedTanzania/elmis-mediator-ffars?branch=development)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f623bcd8d2a74180ba0c0c942d9bdd7a)](https://app.codacy.com/gh/SoftmedTanzania/elmis-mediator-ffars?utm_source=github.com&utm_medium=referral&utm_content=SoftmedTanzania/elmis-mediator-ffars&utm_campaign=Badge_Grade)

An [OpenHIM](http://openhim.org/) mediator for handling system integration  from FFARS to eLMIS.

# Getting Started
Clone the repository and run `npm install`

Open up `src/main/resources/mediator.properties` and supply your OpenHIM config details and save:

```
  mediator.name=eLMIS-Mediator-FFARS
  # you may need to change this to 0.0.0.0 if your mediator is on another server than HIM Core
  mediator.host=localhost
  mediator.port=4000
  mediator.timeout=60000

  core.host=localhost
  core.api.port=8080
  # update your user information if required
  core.api.user=openhim-username
  core.api.password=openhim-password
```

To build and launch our mediator, run

```
  mvn install
  java -jar target/elmis-mediator-ffars-0.1.0-jar-with-dependencies.jar
```