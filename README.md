# TwitterSamples
A java project (work in progress) that connects to the Twitter's [Streaming API] (https://developer.twitter.com/en/docs/tutorials/consuming-streaming-data) and aggregates counts of statuses that contain a given search term.  Aggregate totals are logged hourly.


## Features
* Uses Twitter's HoseBirdClient to connect, and to handle support for GZIP, OAuth, automatic reconnecting with backfills if available, and relevant statistics and events.

## Dependencies 
* The provided Maven pom.xml can be used to bring in dependencies.
* Alternatively, the projects used are 
  * hbc-core 2.2.0 https://github.com/twitter/hbc
  * Apache commons-lang3 3.8.1 https://commons.apache.org/proper/commons-lang/
  * Apache commons-io 2.6 for testing https://commons.apache.org/proper/commons-io/
  * mockito-core 2.2.0 for testing https://site.mockito.org/

## Important!
This project is not finished and is work in progress.  I am waiting for oauth credentials from Twitter so have not been able to run it yet.
