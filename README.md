# Github Top Contributors App
Small demo app that uses the Github GraphQL API and retrieves a list of the top contributors by location.

The application was developed in **koltin** using the **spring boot** non-blocking framework webflux on top of kotlin coroutines and thus the lack of typical reactor types in the code (Mono and Flux).

Because the Github GraphQL API used in the backend to retrieve the top contributors requires authentication the application will first redirect the user to authenticate in Github to retrieve a temporary access token using Oauth2. _Don't worry, you are actually login into Github and not the application itself_.

To check the exposed API take a look at the **_Open API_** documentation either by navigating to _"/swagger-ui.html"_ or clicking on the **API** link in the application header.

You can check a live demo of the application here:

* [Github top contributors](http://githubtopcontributors.fabioandreola.com/)
* [Open API documentation](http://githubtopcontributors.fabioandreola.com/swagger-ui.html)


<img src="https://github.com/fabioandreola/github-top-contributors-app/blob/5ab17b994e18d574282c70c2b583e911c2de04c6/doc/images/app_screenshot.png" width="800"/>

## How to run the application

##### Requirements 

* Java 11+ installed.
* Git.
* A Github account if you want to use the app.
* Docker (optional).

##### First, let's build the app.

1. Clone this repository 
```sh
git clone https://github.com/fabioandreola/github-top-contributors-app.git
```

2. Navigate to the root directory of the cloned repository

3. Build the application

```sh
./gradlew clean build
```

##### Run the app.

After the application is built there are two easy ways to run it from the root directory of the application.

###### If you have docker

```sh
./docker-compose up
```

###### If you only have java 11+

```sh
./gradlew -DCLIENT_ID=530e530b1ebd1806e17b -DCLIENT_SECRET=07d4ba6cdac72e502f12dc8d554ac1134a710dde bootRun
```

###### Too much work?

Just check the application running here: [Github top contributors](http://githubtopcontributors.fabioandreola.com/)

## Performance considerations

The application was built using reactor and kotlin coroutines thus all threads that would be blocked by let's say, calling the Github API, are actually returned to the pool to serve another request while the function waiting for the result is **_suspended_** until it has the data necessary to proceed.

**Example:**

```kotlin
           webclient.post()
                    .uri(apiUrl)
                    .body(BodyInserters.fromValue(getTopContributorsGraphQlQuery(location, chunkSize, fromPageCursor)))
                    .header(AUTHORIZATION, "Bearer $accessToken")
                    .accept(APPLICATION_JSON)
                    .awaitExchange()                 
                    .awaitBody<String>()    // suspend this function until we have the data we need
```

## Improvements

Since this is a small demo app a few items were left off but that should be addressed before deploying to a real production environment.

###### Caching

The Github API has a limit to the number of requests that can be made in a given minute so ideally the app should cache the results in memory so we don't have to fetch _Barcelona top contributors_ all the time.

###### Error handling

Currently, there is no error handling when the Github API fails or the limit exceeds.

###### Tests

Tests are quite basic at the moment and adding blackbox tests would be nice to have. 

###### Demo UI

The UI needs to be fixed to be responsive and display correctly in mobile devices.

###### Newrelic monitoring

Setting up both the Newrelic infrastructure and APM monitoring was quite easy but after adding the Newrelic APM **_javaagent_** to my application start up script I've noticed that an entry was correctly created on my newrelic dashboard but no web transactions could be seen. Also, even though there were no errors both in the application log and in the Newrelic apm logs the application became unresponsive with the agent so I had to remove it. I guess kotlin coroutines or reactive apps are not supported yet?!

Monitoring the server as usual was quite easy:

<img src="https://github.com/fabioandreola/github-top-contributors-app/blob/master/doc/images/newrelic.png" width="800"/>


