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

package de.d3adspace.heimdall.client.initializer;

import de.d3adspace.heimdall.client.SimpleHeimdallClient;
import de.d3adspace.heimdall.client.connection.HeimdallConnection;
import de.d3adspace.heimdall.commons.codec.JSONPacketDecoder;
import de.d3adspace.heimdall.commons.codec.JSONPacketEncoder;
import de.d3adspace.heimdall.commons.utils.NettyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * Initializer for the pipeline.
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

  /**
   * The underlying client.
   */
  private final SimpleHeimdallClient client;

  /**
   * Create a new initializer based on a client.
   *
   * @param client The client.
   */
  public ClientChannelInitializer(SimpleHeimdallClient client) {
    this.client = client;
  }

  public void initChannel(Channel channel) {
    ChannelPipeline pipeline = channel.pipeline();

    ChannelHandler lengthFieldBasedFrameDecoder = NettyUtils
      .createLengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4);
    pipeline.addLast(lengthFieldBasedFrameDecoder);

    ChannelHandler jsonDecoder = new JSONPacketDecoder();
    pipeline.addLast(jsonDecoder);

    ChannelHandler lengthFieldPrepender = NettyUtils.createLengthFieldPrepender(4);
    pipeline.addLast(lengthFieldPrepender);

    ChannelHandler jsonEncoder = new JSONPacketEncoder();
    pipeline.addLast(jsonEncoder);

    ChannelHandler channelHandler = new HeimdallConnection(client);
    pipeline.addLast(channelHandler);
  }
}
