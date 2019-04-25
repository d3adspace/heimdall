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

package de.d3adspace.heimdall.server.initializer;

import de.d3adspace.heimdall.commons.codec.JSONPacketDecoder;
import de.d3adspace.heimdall.commons.codec.JSONPacketEncoder;
import de.d3adspace.heimdall.commons.utils.NettyUtils;
import de.d3adspace.heimdall.server.SimpleHeimdallServer;
import de.d3adspace.heimdall.server.connection.HeimdallConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * Setup for all the connections.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class ServerChannelInitializer extends ChannelInitializer<Channel> {

    /**
     * The underlying server.
     */
    private final SimpleHeimdallServer server;

    /**
     * Create a new initializer based on a server.
     *
     * @param server The server.
     */
    public ServerChannelInitializer(SimpleHeimdallServer server) {
        this.server = server;
    }

    @Override
    public void initChannel(Channel channel) throws Exception {
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

        ChannelHandler channelHandler = new HeimdallConnection(server, channel);
        pipeline.addLast(channelHandler);
    }
}
