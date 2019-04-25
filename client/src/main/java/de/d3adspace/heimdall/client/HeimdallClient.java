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

import de.d3adspace.heimdall.client.handler.PacketHandler;
import org.json.JSONObject;

/**
 * Basic Heimdall client interface.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface HeimdallClient {

    /**
     * Connect to the server.
     */
    void connect();

    /**
     * Disconnect from the server.
     */
    void disconnect();

    /**
     * Add a new subscriber.
     */
    void subscribe(PacketHandler packetHandler);

    /**
     * Remove a subscription.
     */
    void unsubscribe(PacketHandler packetHandler);

    /**
     * Publish an object to the network.
     *
     * @param channelName The channel name.
     * @param jsonObject  The object.
     */
    void publish(String channelName, JSONObject jsonObject);

    /**
     * Perform a request, you will get a response!
     *
     * @param channelName The channel name.
     * @param jsonObject The request.
     * @param response The consumer for the response.
     *//*
    void request(String channelName, JSONObject jsonObject, Consumer<JSONObject> response);*/
}
