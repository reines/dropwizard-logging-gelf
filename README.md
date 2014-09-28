Dropwizard Logging GELF
========

A log appender factory for Dropwizard adding support for appending logs to GELF compatible servers.

[![Build Status](https://api.travis-ci.org/reines/dropwizard-logging-gelf.png)](https://travis-ci.org/reines/dropwizard-logging-gelf)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jamierf.dropwizard/dropwizard-logging-gelf/badge.png)](https://maven-badges.herokuapp.com/maven-central/com.jamierf.dropwizard/dropwizard-logging-gelf)


dropwizard-logging-gelf can be found in maven central.

## Installation

    <dependency>
        <groupId>com.jamierf.dropwizard</groupId>
        <artifactId>dropwizard-logging-gelf</artifactId>
        <version>...</version>
    </dependency>

## Configuration

    logging:
      appenders:
        - type: gelf
          server: localhost:9200
