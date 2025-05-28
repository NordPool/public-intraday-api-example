# Nord Pool Intraday API Example Code #

This repository contains a Java console example application for interaction with Nord Pool Intraday Trading platform. The respective documentation is located at [our Development Portal](https://developers.nordpoolgroup.com/v1.0/docs/id-introduction). 

This sample application uses Java data objects published in [Java Intraday API package](https://maven.pkg.github.com/NordPool/public-intraday-api)

There is also .NET sample application at [https://github.com/NordPool/public-intraday-api-net-example](https://github.com/NordPool/public-intraday-api-net-example). 

## Disclaimer ##

We offer the data transfer object code and client code examples to aid the development against Nord Pool's API at no warranty whatsoever. Clients are solely responsible for separately testing and ensuring that interaction with Nord Pool works according to their own standards.

## Building ##

Before you begin, you should have the library [https://github.com/NordPool/public-intraday-api](https://github.com/NordPool/public-intraday-api) to be available through GitHub packages. 
For more detailed instruction see **Authenticating to GitHub Packages** section of this document.


Then you may import the code in your favorite IDE and build project with **gradle build** command.

All the relevant variables for connecting are located in [application.properties](src/main/resources/application.properties) and Gradle's [build.gradle](build.gradle) configuration files. 
Before running the example, user credentials should be updated to [application.properties](src/main/resources/application.properties):
```
#!
credentials.userName=your_user
credentials.password=your_password
```

These credentials shall be obtained from [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com) prior to running the example.

Additionally, make sure that all the other variables in the [application.properties](src/main/resources/application.properties) file point to correct addresses.
Finally, run the solution with **gradle run** command or by run a startup class [ApplicationMain.java](src/main/java/nps/id/publicapi/java/client/ApplicationMain.java) with your Java IDE.

The program will create two parallel connections that targets both: **Market data** web service and **Trading** web service.
Each connection subscribes to several example topics. It also provides an example of sending messages to Intraday platform.

Every communication step, its results or exceptions are printed in console output window.~~~~

The sequence of actions are located in [AppListener.java](src/main/java/nps/id/publicapi/java/client/startup/AppListener.java) source code, which is triggered once the program has started.

## Authenticating to GitHub Packages ##

See more information on [GitHub docs](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages)

GitHub Packages only supports authentication using a personal access token (classic). 

You need an access token to publish, install, and delete private, internal, and public packages.

You can use a personal access token (classic) to authenticate to GitHub Packages or the GitHub API. When you create a personal access token (classic), you can assign the token different scopes depending on your needs. 

Login and access token can be set in Gradle's configuration [build.gradle](build.gradle) file:
```
credentials {
    username "your_username"
    password "your_github_access_token"
}
```

## Important considerations ##

The current program is using the Spring library and its Websocket Stomp client. The usage of it is at your own discretion. 

In addition, sending heartbeat task created after connection established and refreshing access token are  defined in the WebSocketConnector. That connector can be found from [WebSocketConnector.java](src/main/java/nps/id/publicapi/java/client/connection/WebSocketConnector.java).
Heartbeat interval configuration can be found in [application.properties](src/main/resources/application.properties) **market.data.heartbeat.outgoing.interval** property for Market data web service and **trading.heartbeat.outgoing.interval** for Trading web service.

In [WebSocketConnector.java](src/main/java/nps/id/publicapi/java/client/connection/WebSocketConnector.java) please note the configuration of **maxTextMessageSize** and **maxByteMessageSize**. 
Some messages may be quite large. 

The example uses port 443(secured) for establishing the web socket connection with **Trading** and **Market data** web services. 
If the example doesn't connect to the API, check that the above ports has been opened from your firewall.

## Questions, comments and error reporting ##

Please send questions and bug reports to [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com).