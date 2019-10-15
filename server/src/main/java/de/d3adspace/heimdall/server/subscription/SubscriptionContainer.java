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

package de.d3adspace.heimdall.server.subscription;

import de.d3adspace.heimdall.server.connection.HeimdallConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The container for all subscriptions.
 */
public class SubscriptionContainer {

  /**
   * The underlying Map.
   */
  private final Map<String, List<HeimdallConnection>> subscriptions;

  /**
   * Logger for all subscriptions.
   */
  private final Logger logger;

  /**
   * Create a new container based on a list of already active subscriptions.
   *
   * @param subscriptions The subsscriptions.
   */
  private SubscriptionContainer(Map<String, List<HeimdallConnection>> subscriptions) {
    this.subscriptions = subscriptions;
    this.logger = LoggerFactory.getLogger(SubscriptionContainer.class);
  }

  /**
   * Create an empty container.
   */
  public SubscriptionContainer() {
    this(new ConcurrentHashMap<>());
  }

  /**
   * Add a new subscription.
   *
   * @param channelName The channel name.
   * @param connection  The connection.
   */
  public void addSubscription(String channelName, HeimdallConnection connection) {
    this.logger.info("Got a new subscription on {} from {}.", channelName,
      connection.getRemoteAdress());

    if (!subscriptions.containsKey(channelName)) {
      this.subscriptions.put(channelName, new CopyOnWriteArrayList<>());
    }

    this.subscriptions.get(channelName).add(connection);
  }

  /**
   * Remove a subscriptions.
   *
   * @param channelName The channel name.
   * @param connection  The connection.
   */
  public void removeSubscription(String channelName, HeimdallConnection connection) {
    this.logger.info("{} is unsubscribing from {}.", connection.getRemoteAdress(), channelName);

    this.subscriptions.get(channelName).remove(connection);
  }

  /**
   * Get all subscribers of a channel.
   *
   * @param channelName The channel name.
   * @return The connections.
   */
  public List<HeimdallConnection> getSubscribers(String channelName) {
    return this.subscriptions.get(channelName);
  }
}
