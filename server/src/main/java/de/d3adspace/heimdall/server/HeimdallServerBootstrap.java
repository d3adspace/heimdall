package de.d3adspace.heimdall.server;

import de.d3adspace.heimdall.server.config.HeimdallServerConfig;
import de.d3adspace.heimdall.server.config.HeimdallServerConfigBuilder;

public class HeimdallServerBootstrap {

    private static final String ENVIRONMENT_HEIMDALL_SERVER_HOST = "HEIMDALL_SERVER_HOST";
    private static final String ENVIRONMENT_HEIMDALL_SERVER_PORT = "HEIMDALL_SERVER_PORT";

    public static void main(String[] args) {

        String serverHost = System.getenv(ENVIRONMENT_HEIMDALL_SERVER_HOST);
        int serverPort = Integer.valueOf(System.getenv(ENVIRONMENT_HEIMDALL_SERVER_PORT));

        HeimdallServerConfig config = new HeimdallServerConfigBuilder()
                .setServerHost(serverHost)
                .setServerPort(serverPort)
                .createHeimdallServerConfig();

        HeimdallServer heimdallServer = HeimdallServerFactory.createHeimdallServer(config);
        heimdallServer.start();
    }
}
