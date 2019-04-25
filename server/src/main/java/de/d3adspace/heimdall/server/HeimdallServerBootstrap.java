package de.d3adspace.heimdall.server;

import de.d3adspace.heimdall.server.config.HeimdallServerConfig;
import de.d3adspace.heimdall.server.config.HeimdallServerConfigBuilder;

public class HeimdallServerBootstrap {

    public static void main(String[] args) {

        HeimdallServerConfig config = new HeimdallServerConfigBuilder()
                .setServerHost("localhost")
                .setServerPort(8080)
                .createHeimdallServerConfig();

        HeimdallServer heimdallServer = HeimdallServerFactory.createHeimdallServer(config);
        heimdallServer.start();
    }
}
