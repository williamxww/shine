package com.bow.remoting.netty;

import com.bow.common.exception.ShineException;
import com.bow.common.exception.ShineExceptionCode;
import com.bow.common.pipeline.DefaultServerPipeline;
import com.bow.common.pipeline.DefaultShinePipeline;
import com.bow.common.pipeline.EmptyHandler;
import com.bow.common.pipeline.InvokeServiceHandler;
import com.bow.common.pipeline.SendResponseHandler;
import com.bow.common.pipeline.ShinePipeline;
import com.bow.config.ShineConfig;
import com.bow.remoting.ShineServer;
import com.bow.rpc.Request;
import com.bow.rpc.RequestHandler;
import com.bow.rpc.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty Server
 * @author vv
 * @since 2016/9/3.
 */
public class NettyServer implements ShineServer{

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    /**
     * 最原始的请求处理类
     */
    private RequestHandler requestHandler;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap bootstrap;

    private Channel serverChannel;

    private void bind(final int port) throws Exception {
        NettyHelper.setNettyLoggerFactory();

        bootstrap = new ServerBootstrap();
        NettyServerHandler nettyServerHandler = new NettyServerHandler(this);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ObjectEncoder());
                        p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        p.addLast(nettyServerHandler);
                    }
                });

        ChannelFuture future = bootstrap.bind(port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("----- success to start netty server -----");
            }
        });
        serverChannel = future.channel();
        ChannelFuture closeFuture = serverChannel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("close netty server channel");
            }
        });
    }


    @Override
    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        InvokeServiceHandler invokeHandler = new InvokeServiceHandler(requestHandler);

        ShinePipeline serverPipeline = DefaultServerPipeline.getInstance();
        serverPipeline.addLast(invokeHandler);

    }

    @Override
    public void start() {
        try {
            if(requestHandler==null){
                throw new ShineException("please set requestHandler with NettyServer#setRequestHandler");
            }
            bind(ShineConfig.getServicePort());
        } catch (Exception e) {
            throw new ShineException(ShineExceptionCode.fail,e);
        }
    }


    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if(logger.isInfoEnabled()){
            logger.info("netty server shutdown");
        }
    }
}
