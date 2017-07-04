# Heimdall
Heimdall is supposed to be a lightweight and easy to use pub sub communication framework you 
can build your infrastructure on. 

# Installation / Usage

- Install [Maven](http://maven.apache.org/download.cgi)
- Clone this repo
- Install: ```mvn clean install```

**Maven dependencies**

_Client:_
```xml
<dependency>
    <groupId>de.d3adspace</groupId>
    <artifactId>heimdall-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

_Server:_
```xml
<dependency>
    <groupId>de.d3adspace</groupId>
    <artifactId>heimdall-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

_Commons:_
```xml
<dependency>
    <groupId>de.d3adspace</groupId>
    <artifactId>heimdall-commons</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# Example
_Server:_
```java
package de.d3adspace.heimdall.example;

import de.d3adspace.heimdall.server.HeimdallServer;
import de.d3adspace.heimdall.server.HeimdallServerFactory;
import de.d3adspace.heimdall.server.config.HeimdallServerConfig;
import de.d3adspace.heimdall.server.config.HeimdallServerConfigBuilder;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class HeimdallServerExample {
	
	public static void main(String[] args) {
		HeimdallServerConfig config = new HeimdallServerConfigBuilder()
			.setServerHost("localhost")
			.setServerPort(1337)
			.createHeimdallServerConfig();
		
		HeimdallServer heimdallServer = HeimdallServerFactory.createHeimdallServer(config);
		heimdallServer.start();
	}
}
```

_Client:_
```java
package de.d3adspace.heimdall.example;

import de.d3adspace.heimdall.client.HeimdallClient;
import de.d3adspace.heimdall.client.HeimdallClientFactory;
import de.d3adspace.heimdall.client.annotation.Channel;
import de.d3adspace.heimdall.client.config.HeimdallClientConfig;
import de.d3adspace.heimdall.client.config.HeimdallClientConfigBuilder;
import de.d3adspace.heimdall.client.handler.PacketHandler;
import org.json.JSONObject;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class HeimdallClientExample {
	
	public static void main(String[] args) {
		HeimdallClientConfig config = new HeimdallClientConfigBuilder()
			.setServerHost("localhost")
			.setServerPort(1337)
			.createHeimdallClientConfig();
		
		HeimdallClient client = HeimdallClientFactory.createHeimdallClient(config);
		client.connect();
		
		PacketHandler packetHandler = new PacketHandlerExample();
		client.subscribe(packetHandler);
		
		client.publish("cluster", new JSONObject().put("Hello", "World!"));
		
		client.unsubscribe(packetHandler);
		
		client.disconnect();
	}
}
```
```java
package de.d3adspace.heimdall.example;

import de.d3adspace.heimdall.client.annotation.Channel;
import de.d3adspace.heimdall.client.handler.PacketHandler;
import org.json.JSONObject;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
@Channel("cluster")
public class PacketHandlerExample implements PacketHandler {
	
	public void handlePacket(JSONObject jsonObject) {
		System.out.println("Message in cluster: " + jsonObject);
	}
}
```