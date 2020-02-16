![Java CI](https://github.com/fabioandreola/github-top-contributors-app/workflows/Java%20CI/badge.svg?branch=master)

# Github Top Contributors App
Small demo app that uses the Github GraphQL API and retrieves a list of the top contributors by location.

The application was developed in **koltin** using the **spring boot** non-blocking framework webflux on top of kotlin coroutines and thus the lack of typical reactor types in the code (Mono and Flux).

Because the Github GraphQL API used in the backend to retrieve the top contributors requires authentication the application will first redirect the user to authenticate in Github to retrieve a temporary access token using Oauth2. _Don't worry, you are actually login into Github and not the application itself_.

To check the exposed API take a look at the **_Open API_** documentation either by navigating to _"/swagger-ui.html"_ or clicking on the **API** link in the application header.

You can check a live demo of the application here:

* [Github top contributors](http://githubtopcontributors.fabioandreola.com/)
* [Open API documentation](http://githubtopcontributors.fabioandreola.com/swagger-ui.html)


<img src="https://github.com/fabioandreola/github-top-contributors-app/blob/668e7ae6aef40c1b03dcfe1232f4a94549cda6f1/doc/images/app_screenshot.png" width="800"/>

## How to run the application

##### Requirements 

* Git.
* A Github account if you want to use the app.
* Docker and Docker Compose.

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

After the application is built bootstrap the app using docker-compose.

```sh
./docker-compose up
```

##### Making sure everything is working correctly 

`docker-compose.yml` creates 2 containers, one for the UI exposing the app at port 80 and another one for the backend that is not exposed externally. 

To make sure the app is working correctly open you browser and head to http://localhost. If it is your first time running the app you will be redirected to the login page.

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

Tests are quite basic at the moment and adding blackbox tests would be a nice to have. 

###### Demo UI

The UI needs to be fixed to be responsive and display correctly in mobile devices.

###### Newrelic monitoring

Setting up both the Newrelic infrastructure and APM monitoring was quite easy but after adding the Newrelic APM **_javaagent_** to my application start up script I've noticed that an entry was correctly created on my newrelic dashboard but no web transactions could be seen. Also, even though there were no errors both in the application log and in the Newrelic apm logs the application became unresponsive with the agent so I had to remove it. I guess kotlin coroutines or reactive apps are not supported yet?!

Monitoring the server as usual was quite easy:

<img src="https://github.com/fabioandreola/github-top-contributors-app/blob/master/doc/images/newrelic.png" width="800"/>


