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

package de.d3adspace.heimdall.server.connection;

import de.d3adspace.heimdall.commons.utils.NettyUtils;
import de.d3adspace.heimdall.server.SimpleHeimdallServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Representing a connection of a client to the server.
 */
public class HeimdallConnection extends SimpleChannelInboundHandler<JSONObject> {

  /**
   * The server this connection belongs to.
   */
  private final SimpleHeimdallServer server;

  /**
   * The channel to the client.
   */
  private final Channel channel;

  /**
   * Create a new connection by a channel and its endpoint.
   *
   * @param server  The server.
   * @param channel The channel.
   */
  public HeimdallConnection(SimpleHeimdallServer server, Channel channel) {
    this.server = server;
    this.channel = channel;
  }

  protected void channelRead0(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject)
    throws Exception {
    System.out.println("Received: " + jsonObject);

    this.server.handlePacket(this, jsonObject);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof IOException) {
      NettyUtils.closeWhenFlushed(ctx.channel());
      return;
    }

    cause.printStackTrace();
  }

  /**
   * Send a packet to the client.
   *
   * @param jsonObject The object.
   */
  public void sendPacket(JSONObject jsonObject) {
    this.channel.writeAndFlush(jsonObject);
  }

  /**
   * Get the socket address of the client.
   *
   * @return The remote address.
   */
  public SocketAddress getRemoteAdress() {
    return this.channel.remoteAddress();
  }
}
