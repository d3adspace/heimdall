/*
 * Copyright (c) 2017 D3adspace
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.d3adspace.heimdall.server;

import de.d3adspace.heimdall.commons.utils.NettyUtils;
import de.d3adspace.heimdall.server.config.HeimdallServerConfig;
import de.d3adspace.heimdall.server.connection.HeimdallConnection;
import de.d3adspace.heimdall.server.handler.PacketHandler;
import de.d3adspace.heimdall.server.handler.PacketHandlerFactory;
import de.d3adspace.heimdall.server.initializer.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic Server implementation.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SimpleHeimdallServer implements HeimdallServer {

    /**
     * Config to create the server from.
     */
    private final HeimdallServerConfig config;

    /**
     * The handler for all incoming packets.
     */
    private final PacketHandler packetHandler;

    /**
     * Logger for the server.
     */
    private final Logger logger;

    /**
     * The basic server channel.
     */
    private Channel channel;

    /**
     * The boss group for netty.
     */
    private EventLoopGroup bossGroup;

    /**
     * The worker group for netty.
     */
    private EventLoopGroup workerGroup;

    /**
     * Create a new server by a config.
     *
     * @param config The config.
     */
    SimpleHeimdallServer(HeimdallServerConfig config) {
        this.config = config;
        this.packetHandler = PacketHandlerFactory.createPacketHandler();
        this.logger = LoggerFactory.getLogger(SimpleHeimdallServer.class);
    }

    @Override
    public void start() {
        this.bossGroup = NettyUtils.createEventLoopGroup(1);
        this.workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends ServerChannel> serverChannelClass = NettyUtils.getServerChannelClass();
        ChannelHandler channelHandler = new ServerChannelInitializer(this);

        this.logger.info("I'm going to start a new heimdall server instance on {}:{}",
                this.config.getServerHost(), this.config.getServerPort());

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            channel = serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(serverChannelClass)
                    .childHandler(channelHandler)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .bind(this.config.getServerHost(), this.config.getServerPort())
                    .sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.logger.info("Server started on {}:{}", this.config.getServerHost(),
                this.config.getServerPort());
    }

    @Override
    public void stop() {
        this.channel.close();

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    /**
     * Handling all incoming packets by a connection.
     *
     * @param connection The connection.
     * @param jsonObject The incoming data.
     */
    public void handlePacket(HeimdallConnection connection, JSONObject jsonObject) {
        this.packetHandler.handlePacket(connection, jsonObject);
    }
}
