# Salesforce Bulk Service
[![GitHub release](https://img.shields.io/github/release/prathik/sbs.svg)](https://github.com/prathik/sbs) [![Build Status](https://travis-ci.org/prathik/sbs.svg?branch=master)](https://travis-ci.org/prathik/sbs) [![Coverage Status](https://coveralls.io/repos/github/prathik/sbs/badge.svg?branch=master)](https://coveralls.io/github/prathik/sbs?branch=master)

## Features

* Bulk upload CSV to Salesforce via a REST endpoint
* Monitor status of upload job
* Auto-retry on failure
* Supports proxy
* Swagger support, go to api/swagger.json

## Running

Do `mvn install` on the parent folder. Go to `api` folder and run `mvn package`, copy the generated `war` file into `jetty` or `tomcat`.