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

package de.d3adspace.heimdall.client.connection;

import de.d3adspace.heimdall.client.SimpleHeimdallClient;
import de.d3adspace.heimdall.commons.utils.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Representing a connection.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class HeimdallConnection extends SimpleChannelInboundHandler<JSONObject> {

    /**
     * The underlying client.
     */
    private final SimpleHeimdallClient client;

    /**
     * Logger for the connection.
     */
    private final Logger logger;

    /**
     * Create a new connection based on a client.
     *
     * @param client The client.
     */
    public HeimdallConnection(SimpleHeimdallClient client) {
        this.client = client;
        this.logger = LoggerFactory.getLogger(HeimdallConnection.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JSONObject jsonObject)
            throws Exception {
        client.handlePacket(jsonObject);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            NettyUtils.closeWhenFlushed(ctx.channel());
            return;
        }

        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Lost connection to the server.");
    }
}
