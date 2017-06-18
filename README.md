# Salesforce Bulk Service
[![GitHub release](https://img.shields.io/github/release/prathik/sbs.svg)](https://github.com/prathik/sbs) [![Build Status](https://travis-ci.org/prathik/sbs.svg?branch=master)](https://travis-ci.org/prathik/sbs) [![Coverage Status](https://coveralls.io/repos/github/prathik/sbs/badge.svg?branch=master)](https://coveralls.io/github/prathik/sbs?branch=master)

## Features

* Bulk upload CSV to Salesforce via a REST endpoint
* Monitor status of upload job
* Auto-retry on failure
* Supports proxy
* Swagger support, go to api/swagger.json

## Running

Do `mvn install` on the parent folder.

Go to `api` folder and run `mvn package`, copy the generated `war` file into `jetty` or `tomcat`.

### Docker

Easier way to run is via `Docker`.

#### Building

`docker build -t sbs .`

#### Starting the server

`docker run -p8080:8080 -it <container-id>`

## Using the service

An example `curl` call

`curl -v -F file=@data.csv http://localhost:8080/api-1.0-SNAPSHOT/api/upload/SBS__c/insert`

Where `data.csv` is the file that has data which is going to be inserted into a Salesforce object called `SBS__c`.

Another way is to generate client using swagger. You can find the swagger spec at `http://localhost:8080/api-1.0-SNAPSHOT/api/swagger.json`.