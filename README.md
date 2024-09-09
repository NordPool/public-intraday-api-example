# Nord Pool Intraday API Example Code #

This repository contains a Java console example application for interaction with Nord Pool Intraday Trading platform. The respective documentation is located at [our Development Portal](https://developers.nordpoolgroup.com/v1.0/docs/id-introduction). 

This sample application uses Java data objects published in [Java Intraday API package](https://maven.pkg.github.com/NordPool/public-intraday-api)

There is also .NET sample application at [https://github.com/NordPool/public-intraday-api-net-example](https://github.com/NordPool/public-intraday-api-net-example). 

## Disclaimer ##

We offer the data transfer object code and client code examples to aid the development against Nord Pool's API at no warranty whatsoever. Clients are solely responsible for separately testing and ensuring that interaction with Nord Pool works according to their own standards.

## Building ##

Before you begin, you should have the library [https://github.com/NordPool/public-intraday-api](https://github.com/NordPool/public-intraday-api) to be available through GitHub packages. 

Then you may import the code in your favorite IDE and fetch all dependencies with **mvn clean install**.

All the revelant variables for connecting are located in **application.properties** and Maven's **~\.m2\settings.xml** configuration files. 
Before running the example, user credentials should be updated to **application.properties**:
```
#!
credentials.userName=your_user
credentials.password=your_password
```

These credentials shall be obtained from [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com) prior to running the example.

Additionally, make sure that all the other variables in the application.properties file point to correct addresses.
Finally, build the solution with IntelliJ, or any other Java IDE and run the program using startup class **ApplicationMain.java**.

The program will create two parallel connections that targets both: new **PMD API** web service and old **Middleware** web service.
Each connection subscribes to several example topics. It also provides an exanmple of sending messages to Intraday platform.

Every communication step, its results or exceptions are printed in console output window.

The sequence of actions are located in **AppListener.java** source code, which is triggered once the program has started.

## Authenticating to GitHub Packages ##

See more information on [GitHub docs](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages)

GitHub Packages only supports authentication using a personal access token (classic). 

You need an access token to publish, install, and delete private, internal, and public packages.

You can use a personal access token (classic) to authenticate to GitHub Packages or the GitHub API. When you create a personal access token (classic), you can assign the token different scopes depending on your needs. 

Login and access token can be set in Maven's configuration **~\.m2\settings.xml** file:
```
#!
<servers>
    <server>
        <id>public-intraday-api-repo</id>
        <username>your_username</username>
        <password>your_access_token</password>
    </server>
</servers>
```
An id public-intraday-api-repo indicates identifier of custom repository for accessing [Java Intraday API package](https://maven.pkg.github.com/NordPool/public-intraday-api).

More informations about **~\.m2\settings.xml** file can be found at [Settings reference page](https://maven.apache.org/settings.html#servers).

## Important considerations ##

The current program is using the Spring library and its Websocket Stomp client. The usage of it is at your own discretion. 

In **WebSocketConnector.java** please note the configuration of maxTextMessageSize and maxByteMessageSize. 
Some messages may be quite large. 

The example uses ports 8083/443(secured) for establishing the web socket connection with **Middleware** web service and ports 80/443(secured) for establishing web socket connection with **PMD** web service. 
If the example doesn't connect to the API, check that the above ports has been opened from your firewall.

## Questions, comments and error reporting ##

Please send questions and bug reports to [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com).

## SSL configuration: 

Change useSsl property value from false to true.
```
#!
pmd.web.socket.useSsl=true

middleware.web.socket.useSsl=true
```