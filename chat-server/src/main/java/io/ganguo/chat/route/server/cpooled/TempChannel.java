package io.ganguo.chat.route.server.cpooled;

import io.ganguo.chat.core.protocol.Commands;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMResponse;
import io.ganguo.chat.route.biz.bean.ClientType;
import io.ganguo.chat.route.biz.dto.UserDTO;
import io.ganguo.chat.route.biz.entity.User;
import io.ganguo.chat.route.server.Server2RouteInitializer;
import io.ganguo.chat.route.server.config.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by James on 2015/11/23 0023.
 */
public class TempChannel {

    private TempChannel(){

        Channel channel = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new Server2RouteInitializer());

            ChannelFuture future = bootstrap.connect("127.0.0.1", 10006);
            // awaitUninterruptibly() 等待连接成功
            channel = future.awaitUninterruptibly().channel();
            login(channel,"","123123");
            //        login(channel,username,password);
            //       sendMessage(channel);
//            future.channel().closeFuture().awaitUninterruptibly();
        } finally {
            //         group.shutdownGracefully();
        }

       this.channel = channel;

    }

    private Channel channel;

    public Channel getChannel() {
        return channel;
    }


    private final static class TempChannelHoder{
        public static TempChannel tempChannel = new TempChannel();
    }

    public static TempChannel getInstance(){return TempChannelHoder.tempChannel;}

    private void login(Channel channel,String username,String password) {
        User user = new User();
        user.setClientType(ClientType.LINUX.value());
        user.setAccount(username);
        user.setPassword(password);
        user.setcServerIp("127.0.0.1:"+ Constant.PORT1.getValue());
        IMResponse resp = new IMResponse();
        Header header = new Header();
        header.setHandlerId(Handlers.USER);
        header.setCommandId(Commands.LOGIN_REQUEST);
        resp.setHeader(header);
        resp.writeEntity(new UserDTO(user));

        channel.writeAndFlush(resp).awaitUninterruptibly();
    }

}
