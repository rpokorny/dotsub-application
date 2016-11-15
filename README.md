# Dotsub Job Application Project
Author: Ross Pokorny

This repository contains my submission for a coding exercise given to me as part
of the application process at Dotsub.

## Summary
This project consists of a simple file upload page which also displays a table
showing the files that have been uploaded.  The backend is built using Spring
Boot, Jersey, and jOOQ.  The fronted uses React, Immutable, and Kefir.  Note
that some of the infrastructure code, especially on the frontend, has been
copied from other projects where I originally authored it, including
[techexchange-frp-todolist](https://github.com/rpokorny/techexchange-frp-todolist)
and [ozp-rest](https://github.com/ozone-development/ozp-rest).

## Usage
The backend and frontend of this application are separate, located in the
`backend` and `frontend` directories respectively.  They each have their own set
of commands necessary to build and run them and they need to both be running
simultaneously in order to have a functioning system

### Backend
To run the backend in development mode, go into the `backend` directory and
execte `./mvnw spring-boot:run`.  To build the backend, execute` ./mvnw clean
install`.  Once the backend is built, it can be executed  using `java -jar
target/dotsub-application-1.0.0.jar`.

The backend includes unit tests which run as part of the build and which can
also be run using `./mvnw test`.  These unit tests test the two primary parts of
the codebase, the FileResource (a JAX-RS resource class) and the
FileServiceImpl (a service class containing the persistence management logic).

When running, the backend uses an in-memory H2 database along with an
`uploaded-files` directory which is created in the server's current working
directory.  Note that if this directory does not already exist, the server must
have write access to the current directory in order to create it.

### Frontend
The frontend is a create-react-app application and can be built using the
typical commands thereof.  To run it, first ensure that node.js and npm are
installed.  The app has been tested on node 7.0.0 and npm 3.10.9.  Then before
running other build commands, run `npm install` to download dependencies.

To run the server in development mode, execute `npm start`.  To build the
codebase, run `npm run build`.  The build code can be served using
`pushstate-server`, which must be installed separately.

Like the backend, the frontend includes automated unit tests.  These can be run
using `npm test`.  Tests have been written to cover most of the code that is
specific to this project.  Tests for code copied in from other projects have
been omitted.

## Design Considerations
This application was written primarily using technologies and paradigms that I
have gravitated towards over time.  Here I will explain the choices that I made:

### Backend
* Spring - In its simplest usage, Spring is a dependency injection container.
    Dependency injection is a useful tool for writing clean Java code.  In
    particular, it is crucial when writing code that must undergo unit testing,
    as it allows classes to be used in isolation with controlled mocks filling
    in for their dependencies.
* JAX-RS - JAX-RS is my preferred REST framework due to the way it allows APIs
    to be specified declaratively and the way that it splits up resource
    definition, exception handling, and representation reading and writing.
* jOOQ - After years of experience with heavier ORMs such as hibernate, I have
    recently moved more towards "micro-ORMs" such as jOOQ.  jOOQ avoids the
    error-prone relationship abstractions offered by heavier tools and instead
    focuses on a very powerful, typesafe SQL DSL which allows the developer to
    focus on more performant database usage.

### Frontend
* Functional Reactive Programming using React, Kefir, and Immutable - I am a fan
    of functional programming and learned of the patterns for using functional
    reactive programming in browser UIs several years ago.  I original learned
    these patterns through the Elm language and saw that they could be cleanly
    adapted to JavaScript with the right library support.  Having already been
    familiar with React and Immutable, I selected Kefir as my FRP library and
    created my own take on the "Elm Architecture".  I have used this pattern in
    several applications including a [demonstration
    application](https://github.com/rpokorny/techexchange-frp-todolist) that I
    created as part of a technical exchange presentation where I shared the
    pattern with my co-workers.

## Potential Future Work
Given more time, there are a few enhancements that I could make to this
codebase.  See the list below:

1. Backend Integration Tests: Automated tests of the entire server running in a
near-production state would be beneficial.  These could be written using either
Spring Boot's testing framework or using an external tool such as JMeter.
2. Improved Error Messages: Backend validation errors currently produce
relatively lengthy, SQL-oriented error messages.  More work could be done in
order to catch the underlying exceptions and wrap them in something more
user-friendly.
