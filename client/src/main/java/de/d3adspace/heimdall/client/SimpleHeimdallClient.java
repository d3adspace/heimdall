/*
 * Copyright (c) 2017 - 2019 D3adspace
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

package de.d3adspace.heimdall.client;

import de.d3adspace.heimdall.client.config.HeimdallClientConfig;
import de.d3adspace.heimdall.client.handler.PacketHandler;
import de.d3adspace.heimdall.client.handler.SubscriptionHandler;
import de.d3adspace.heimdall.client.initializer.ClientChannelInitializer;
import de.d3adspace.heimdall.commons.HeimdallMessageFields;
import de.d3adspace.heimdall.commons.action.Action;
import de.d3adspace.heimdall.commons.utils.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Basic client implementation.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SimpleHeimdallClient implements HeimdallClient {

    /**
     * The configuration for the client.
     */
    private final HeimdallClientConfig config;

    /**
     * The subscription handler.
     */
    private final SubscriptionHandler subscriptionHandler;

    /**
     * The Logger for the client.
     */
    private final Logger logger;

    /**
     * Netty worker.
     */
    private EventLoopGroup workerGroup;

    /**
     * The connection channel to the server.
     */
    private Channel channel;

    /**
     * Create a client by the config.
     *
     * @param config The config.
     */
    SimpleHeimdallClient(HeimdallClientConfig config) {
        this.config = config;
        this.subscriptionHandler = new SubscriptionHandler(this);
        this.logger = LoggerFactory.getLogger(SimpleHeimdallClient.class);
    }

    @Override
    public void connect() {

        workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends Channel> clientChannelClazz = NettyUtils.getChannel();

        logger.info("Connecting to server {}:{}", config.getServerHost(),
                config.getServerPort());

        Bootstrap bootstrap = new Bootstrap();

        ChannelFuture channelFuture = bootstrap
                .group(workerGroup)
                .channel(clientChannelClazz)
                .handler(new ClientChannelInitializer(this))
                .option(ChannelOption.TCP_NODELAY, true)
                .connect(config.getServerHost(), config.getServerPort());

        channelFuture.awaitUninterruptibly(5, TimeUnit.SECONDS);
        channel = channelFuture.channel();

        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("Connected to server {}:{}", config.getServerHost(),
                        config.getServerPort());
            } else {
                logger.error("Couldn't connect to the server: {}", future.cause().getMessage());
            }
        });
    }

    @Override
    public void disconnect() {
        subscriptionHandler.unregisterPacketHandlers();

        channel.close();

        workerGroup.shutdownGracefully();
    }

    @Override
    public void subscribe(PacketHandler packetHandler) {
        subscriptionHandler.registerPacketHandler(packetHandler);
    }

    @Override
    public void unsubscribe(PacketHandler packetHandler) {
        subscriptionHandler.unregisterPacketHandler(packetHandler);
    }

    @Override
    public void publish(String channelName, JSONObject jsonObject) {
        jsonObject.put(HeimdallMessageFields.MESSAGE_CHANNEL_NAME, channelName);

        if (!jsonObject.has(HeimdallMessageFields.MESSAGE_ACTION_CODE)) {
            jsonObject.put(HeimdallMessageFields.MESSAGE_ACTION_CODE, Action.BROADCAST.getActionId());
        }

        channel.writeAndFlush(jsonObject);
    }

    /**
     * Delegate a packet to the belonging handler.
     *
     * @param jsonObject The object
     */
    public void handlePacket(JSONObject jsonObject) {
        String channelName = (String) jsonObject.remove(HeimdallMessageFields.MESSAGE_CHANNEL_NAME);

        subscriptionHandler.handlePacket(channelName, jsonObject);
    }
}
