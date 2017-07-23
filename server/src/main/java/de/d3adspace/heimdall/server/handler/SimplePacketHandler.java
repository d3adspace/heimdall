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

package de.d3adspace.heimdall.server.handler;

import de.d3adspace.heimdall.commons.action.Action;
import de.d3adspace.heimdall.server.connection.HeimdallConnection;
import de.d3adspace.heimdall.server.subscription.SubscriptionContainer;
import org.json.JSONObject;

import java.util.List;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SimplePacketHandler implements PacketHandler {

    /**
     * Registry for all subscriptions.
     */
    private final SubscriptionContainer subscriptionContainer;

    /**
     * Create a new handler based on a container of subscriptions.
     *
     * @param subscriptionContainer The subscription container.
     */
    SimplePacketHandler(SubscriptionContainer subscriptionContainer) {
        this.subscriptionContainer = subscriptionContainer;
    }

    @Override
    public void handlePacket(HeimdallConnection connection, JSONObject jsonObject) {
        int actionId = (int) jsonObject.remove("actionId");
        Action action = Action.getAction(actionId);

        this.handleAction(connection, jsonObject, action);
    }

    /**
     * Handle an action internal.
     *
     * @param connection The connection.
     * @param jsonObject The packet to handle.
     * @param action     The action to handle.
     */
    private void handleAction(HeimdallConnection connection, JSONObject jsonObject, Action action) {
        if (action == Action.SUBSCRIBE) {
            String channelName = (String) jsonObject.remove("channelName");

            this.subscriptionContainer.addSubscription(channelName, connection);
        } else if (action == Action.UNSUBSCRIBE) {
            String channelName = (String) jsonObject.remove("channelName");

            this.subscriptionContainer.removeSubscription(channelName, connection);
        } else if (action == Action.BROADCAST) {
            String channelName = jsonObject.getString("channelName");

            List<HeimdallConnection> subscribers = this.subscriptionContainer
                    .getSubscribers(channelName);

            if (subscribers == null) {
                return;
            }

            for (HeimdallConnection subscriber : subscribers) {
                if (subscriber == connection) {
                    continue;
                }

                subscriber.sendPacket(jsonObject);
            }
        }
    }
}
