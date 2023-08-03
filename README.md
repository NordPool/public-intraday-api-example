# Nord Pool Intraday API Example Code #

This repository contains an example client for interaction with Nord Pool Intraday Trading platform. The respective documentation is located at [our Development Portal](https://developers.nordpoolgroup.com/v1.0/docs/id-introduction). 

We have an example code for .NET available at [https://github.com/NordPool/public-intraday-api-net-example](https://github.com/NordPool/public-intraday-api-net-example). 

## Disclaimer ##

We offer the data transfer object code and client code examples to aid the development against Nord Pool's API at no warranty whatsoever. Clients are solely responsible for separately testing and ensuring that interaction with Nord Pool works according to their own standards.

## Building ##

Before you begin, you should have the library  [https://github.com/NordPool/public-intraday-api](https://github.com/NordPool/public-intraday-api) to be available through GitHub packages. 

Then you may import the code in your favorite IDE and execute **gradle build**.

In order to connect, **you must edit the src\main\resources\example.properties** file:
```
#!
sso.user=your_user
sso.password=your_password
```
These credentials shall be obtained from [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com) prior to running the example.

Additionally, make sure that all the properties in the file point to correct addresses.
Finally, either run the program using **gradle bootRun** or run the class PublicApiApplication.java.

The program will connect to the platform and subscribe to several topics. It also will send an invalid order so an error reply will come back from the system. The data received from the system will be pretty-printed as JSON in the console. Please note that for clarity we truncate some output that is too large. 

The sequence of actions are located in **com/nordpool/intraday/publicapi/example/startup/StartupListener.java** source code, which is triggered once the program has started.

## Authenticating to GitHub Packages ##

See more information on [GitHub docs](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages)

GitHub Packages only supports authentication using a personal access token (classic). 

You need an access token to publish, install, and delete private, internal, and public packages.

You can use a personal access token (classic) to authenticate to GitHub Packages or the GitHub API. When you create a personal access token (classic), you can assign the token different scopes depending on your needs. 

## Important considerations ##

The current program is using the Spring library and its Websocket Stomp client. The usage of it is at your own discretion. 

In **com/nordpool/intraday/publicapi/example/service/connection/WebSocketConnector.java** please note  the configuration of maxTextMessageSize. Some messages may be quite large. 

## Questions, comments and error reporting ##

Please send questions and bug reports to [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com).

## SSL configuration: Change property value web.socket.usessl from false to true