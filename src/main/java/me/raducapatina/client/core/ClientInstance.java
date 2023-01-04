package me.raducapatina.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.application.Platform;
import me.raducapatina.client.ResourceClientProperties;
import me.raducapatina.client.data.User;
import me.raducapatina.client.gui.Gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientInstance {

    private static ClientInstance instance = null;

    private Gui gui = null;

    private ExecutorService networkService = Executors.newSingleThreadExecutor();
    private ClientRequestHandler requestHandler = new ClientRequestHandler();

    private User selfUser = null;

    private EventLoopGroup group;

    private ClientInstance() {
        this.requestHandler.addRequestTemplate("AUTHENTICATION", new ClientRequestHandler.AuthenticationTemplate());
        this.requestHandler.addRequestTemplate("GET_SELF_USER", new ClientRequestHandler.GetSelfUser(this.selfUser));
    }

    public static synchronized ClientInstance getInstance() {
        if (instance == null)
            instance = new ClientInstance();
        return instance;
    }

    public void start() {

        this.gui = Gui.getInstance();
        this.networkService.submit(() -> {

            group = new NioEventLoopGroup();
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    gui.setScene("loginScreen");
                                    ClientInstance.getInstance().getRequestHandler().setCtx(ctx);

                                    Platform.runLater(() -> {
                                        gui.getLoginController().login_signin_button.setOnAction(event -> requestHandler.sendRequest(
                                                "AUTHENTICATION",
                                                ctx,
                                                new String[]{gui.getLoginController().login_username_field.getText(), gui.getLoginController().login_password_field.getText()}
                                        ));
                                        //gui.getDashboardController().dashboard_webpane_1 = new Gui.ClientWebView("/sources/loading.html");
                                        //gui.getDashboardController().dashboard_webpane_2 = new Gui.ClientWebView("/sources/loading.html");
                                        //gui.getDashboardController().dashboard_webpane_3 = new Gui.ClientWebView("/sources/loading.html");
                                    });
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println(msg);
                                    requestHandler.onMessage(ctx,msg);
                                }
                            });
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            cause.printStackTrace();
                            assert false : cause.getMessage();
                        }
                    });
            try {
                clientBootstrap.connect(
                                (String) ResourceClientProperties.getInstance().getObject("host"),
                                /*(Integer) ResourceClientProperties.getInstance().getObject("port")*/8080)
                        .sync().channel();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void stopApplication() {
        group.shutdownGracefully();
        networkService.shutdownNow();
        Platform.exit();
        System.exit(0); // just to be sure
    }

    public synchronized ClientRequestHandler getRequestHandler() {
        return requestHandler;
    }
}