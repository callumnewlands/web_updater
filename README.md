# Web Page Update Watcher

![Java CI](https://github.com/callumnewlands/web_updater/workflows/Java%20CI/badge.svg)

## Prerequisites
* [Maven](https://maven.apache.org/)
* Java (JRE) 13
* (_optional_) [npm](https://www.npmjs.com/)

## CI
This repo is configured with GitHub actions for CI to automatically run unit tests upon push,
to prevent a commit from triggering 
these tests include the following string in the commit message ```skip-ci```

## Running the Resource Watcher
To run the resource watcher (watches src/main/resources for updates to html/css/js files and automatically copies them 
to the target directory) run ```npm run watch``` in the project root directory

## Rebuild the Project in IntelliJ IDEA
After making changes to the code, ```Ctrl+F9``` will rebuild the application whilst it is running

## Running the Tests
To run the unit tests for the application run ```mvn test``` in the project root directory

## Running the Application
To run the application run ```mvn spring-boot:run``` project root directory

## Heroku
This app is currently available on Heroku at https://website-updater.herokuapp.com/

## Author
Callum Newlands