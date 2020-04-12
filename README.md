# Escher

![Java CI with Gradle](https://github.com/moaxcp/escher/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

Escher is a fork from the original [escher](https://sourceforge.net/projects/escher/) project. It is an
X11 client implementation written in java. This project directly connects to the X11 server rather than binding to a 
native library. Applications using this library do not need to worry about compatibility with a native library.

# Versions

## 0.4.0-SNAPSHOT

### Goals of release

[] add github actions
  [] run test
  [] sonar scan
  [] publish to github
  [] publish to maven central

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