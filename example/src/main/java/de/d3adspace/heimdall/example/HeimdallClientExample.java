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

package de.d3adspace.heimdall.example;

import de.d3adspace.heimdall.client.HeimdallClient;
import de.d3adspace.heimdall.client.HeimdallClientFactory;
import de.d3adspace.heimdall.client.config.HeimdallClientConfig;
import de.d3adspace.heimdall.client.config.HeimdallClientConfigBuilder;
import de.d3adspace.heimdall.client.handler.PacketHandler;
import org.json.JSONObject;

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
