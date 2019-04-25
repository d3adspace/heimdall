/*
 * Copyright (c) 2017 -2019 D3adspace
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

package de.d3adspace.heimdall.client.handler;

import de.d3adspace.heimdall.client.SimpleHeimdallClient;
import de.d3adspace.heimdall.client.annotation.Channel;
import de.d3adspace.heimdall.commons.action.Action;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The handler of all subscriptions.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SubscriptionHandler {

    /**
     * The underlying Map of all packet handlers and their channels.
     */
    private final Map<String, List<PacketHandler>> packetHandlers;

    /**
     * The client to handle subscriptions for.
     */
    private final SimpleHeimdallClient client;

    /**
     * The Logger for the subscription handler.
     */
    private final Logger logger;

    /**
     * Create a new subscription handler.
     *
     * @param client The client.
     */
    public SubscriptionHandler(SimpleHeimdallClient client) {
        this.client = client;
        this.packetHandlers = new ConcurrentHashMap<>();
        this.logger = LoggerFactory.getLogger(SubscriptionHandler.class);
    }

    /**
     * Handle an incoming packet.
     *
     * @param channelName The channel
     * @param jsonObject  The packet.
     */
    public void handlePacket(String channelName, JSONObject jsonObject) {
        List<PacketHandler> packetHandlers = this.packetHandlers.get(channelName);

        if (packetHandlers == null) {
            return;
        }

        packetHandlers.forEach(packetHandler -> packetHandler.handlePacket(jsonObject));
    }

    /**
     * Register a new packet handler.
     *
     * @param packetHandler The handler.
     */
    public void registerPacketHandler(PacketHandler packetHandler) {
        Channel channel = packetHandler.getClass().getAnnotation(Channel.class);

        this.logger.info("Subscribing to channel {}", channel.value());

        if (!this.packetHandlers.containsKey(channel.value())) {
            this.packetHandlers.put(channel.value(), new CopyOnWriteArrayList<>());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("actionId", Action.SUBSCRIBE.getActionId());
            jsonObject.put("channelName", channel.value());

            this.client.publish(channel.value(), jsonObject);
        }

        this.packetHandlers.get(channel.value()).add(packetHandler);
    }

    /**
     * Unregister a packet handler.
     *
     * @param packetHandler The handler.
     */
    public void unregisterPacketHandler(PacketHandler packetHandler) {
        Channel channel = packetHandler.getClass().getAnnotation(Channel.class);

        this.logger.info("Unsubscribing from {}", channel.value());

        this.packetHandlers.get(channel.value()).remove(packetHandler);

        if (this.packetHandlers.get(channel.value()).isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("actionId", Action.UNSUBSCRIBE.getActionId());
            jsonObject.put("channelName", channel.value());

            this.client.publish(channel.value(), jsonObject);

            this.packetHandlers.remove(channel.value());
        }
    }

    /**
     * Unregister all known packet handlers.
     */
    public void unregisterPacketHandlers() {
        for (String channelName : this.packetHandlers.keySet()) {
            this.logger.info("Unsubscribing from {}", channelName);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("actionId", Action.UNSUBSCRIBE.getActionId());
            jsonObject.put("channelName", channelName);

            this.client.publish(channelName, jsonObject);
        }

        this.packetHandlers.clear();
    }
}
