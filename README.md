# Escher

![Java CI with Gradle](https://github.com/moaxcp/escher/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.moaxcp%3Aescher&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.moaxcp%3Aescher)

Escher is a fork from the original [escher](https://sourceforge.net/projects/escher/) project. It is an
X11 client implementation written in java. This project directly connects to the X11 server rather than binding to a 
native library. Applications using this library do not need to worry about compatibility with a native library.

# Development Ending

After working on this library for a few weeks I am deciding to stop. This library has too many issues for me to deal 
with. This library does not follow basic encapsulation and lose-coupling. Multithreaded support is majorly flawed. 
There are too many failed attempts at making things better in the existing classes to deal with. A new x11 client 
should be developed for java which abstracts different levels of functionality.

# Versions

## 0.5.0

Updated `XAuthority` to properly detect the Auth Family. There were also some bugs when reading the file. Referencing 
[libXau](https://gitlab.freedesktop.org/xorg/lib/libxau/-/tree/master) made debugging much easier. 

## 0.4.0

### Goals of release

- [x] setup [reckon](https://github.com/ajoberstar/reckon) plugin
- [x] add github actions
  - [x] run test
  - [x] sonar scan
  - [x] publish release to github
  - [x] publish snapshots to maven central
  - [x] publish release to maven central
- [x] fix new issues in sonar

### Changes

The first release from this fork. Includes previously unreleased code from the original sourceforge repository. A lot
has changed in the unreleased code but this is a summary of what was changed in this fork:

* converted project to gradle
* fixed critical bug where all Input events where LAST_EVENT
* added demo code back in as a separate sourceSet
* added unit tests using junit5!
* added XephyrRunner which can be used to start and X11 server before tests and stop it after tests.
* added integration tests!
* demo code can now be run from the demoTest source set.
* Standardized exceptions on two RuntimeExceptions (X11ClientException and X11ServiceException)
  * The old exceptions were "checked" exceptions or extensions of Error which makes error handling tedious
* Added use of [junixsocket](https://github.com/kohlschutter/junixsocket) since unixsocket is the dominate means of 
connecting
* small changes to api to use standard camelCase and allow demos to run
* Added CI through github actions
* Added project to sonarqube