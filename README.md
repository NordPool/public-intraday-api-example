# Nord Pool Intraday API Example Code #

This repository contains an example client for interaction with Nord Pool Intraday Trading platform. The respective documentation is located at [our Development Portal](https://developers.nordpoolgroup.com/v1.0/docs/id-introduction). 

## Disclaimer ##

We offer the data transfer object code and client code examples to aid the development against Nord Pool’s API at no warranty whatsoever. Clients are solely responsible for separately testing and ensuring that interaction with Nord Pool works according to their own standards.

## Building ##

Before you begin, you should have the library  [https://bitbucket.org/nordpoolspot/public-intraday-api](https://bitbucket.org/nordpoolspot/public-intraday-api) to be available in your local Maven repository. 

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

#Important considerations#

The current program is using the Spring library and its Websocket Stomp client. The usage of it is at your own discretion. 

In **com/nordpool/intraday/publicapi/example/service/connection/WebSocketConnector.java** please note  the configuration of maxTextMessageSize. Some messages may be quite large. 

## Questions, comments and error reporting ##

Please send questions and bug reports to [idapi@nordpoolgroup.com](mailto:idapi@nordpoolgroup.com).

## SSL configuration: Change "ws" to "wss" property value web.socket.protocol=wss
## Configure truststore and keystore for SSL. Template looks like jetty-websocket-http.xml:
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE Configure PUBLIC "-//Jetty//Configure//EN" "http://www.eclipse.org/jetty/configure_9_3.dtd">

<Configure class="org.eclipse.jetty.client.HttpClient">
    <Arg>
        <New class="org.eclipse.jetty.util.ssl.SslContextFactory">
            <Set name="trustAll" type="java.lang.Boolean">false</Set>
            <Call name="addExcludeProtocols">
                <Arg>
                    <Array type="java.lang.String">
                        <Item>TLS/0.1</Item>
                    </Array>
                </Arg>
            </Call>
            <Call name="setKeyStorePath">
                <Arg>
                    path/
                </Arg>
            </Call>
            <Call name="setKeyStorePassword">
                <Arg>
                    KeyStorePass
                </Arg>
            </Call>
            <Call name="setKeyManagerPassword">
                <Arg>
                    KeyManagerPass
                </Arg>
            </Call>
            <Call name="setTrustStorePath">
                <Arg>
                    trust/store/path
                </Arg>
            </Call>
            <Call name="setTrustStorePassword">
                <Arg>
                    TrustStorePass
                </Arg>
            </Call>
        </New>
    </Arg>
    <Set name="connectTimeout">5555</Set>
    <Set name="executor">
        <New class="org.eclipse.jetty.util.thread.QueuedThreadPool">
            <Set name="name">XmlBasedClient@</Set>
        </New>
    </Set>
</Configure>