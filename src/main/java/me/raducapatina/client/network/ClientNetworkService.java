package me.raducapatina.client.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import me.raducapatina.client.core.ClientInstance;
import me.raducapatina.client.data.Article;
import me.raducapatina.client.data.User;
import me.raducapatina.client.gui.Gui;
import me.raducapatina.client.util.ResourceClientMessages;
import me.raducapatina.client.util.ResourceClientProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The request system works like REST but without using HTTP. The procedure is
 * make up of one "question" and one "answer". Works the same for outbound and inbound
 * requestsTemplates.
 *
 * @author Radu
 */
public class ClientNetworkService {

    private static final Logger logger = LogManager.getLogger(ClientNetworkService.class);
    private static final Level MESSAGE = Level.forName("MESSAGE", 450);

    private ExecutorService networkService = Executors.newSingleThreadExecutor();
    private EventLoopGroup group;
    private Map<String, IRequest> requestsTemplates = new HashMap<>();
    private List<Packet> waitingOutboundPackets = new ArrayList<>();

    // client only variables
    private ChannelHandlerContext ctx;

    public ClientNetworkService() {
        this
                .addRequestTemplate("AUTHENTICATION", new Authentication())
                .addRequestTemplate("GET_SELF_USER", new GetSelfUser())
                .addRequestTemplate("GET_ARTICLES", new GetMainPageArticles())

                // ADMIN
                .addRequestTemplate("ADMIN_ADD_USERS", new AdminAddUsers())
                .addRequestTemplate("ADMIN_GET_USERS", new AdminGetUsers())
                .addRequestTemplate("ADMIN_DELETE_USERS", new AdminDeleteUsers())

                .addRequestTemplate("ADMIN_ADD_SUBJECTS", new AdminAddSubjects())
                .addRequestTemplate("ADMIN_GET_SUBJECTS", new AdminGetSubjects())
                .addRequestTemplate("ADMIN_DELETE_SUBJECTS", new AdminDeleteSubjects())

                .addRequestTemplate("ADMIN_GET_STUDENTS", new AdminGetStudents())
                .addRequestTemplate("ADMIN_GET_TEACHERS", new AdminGetTeachers())

                .addRequestTemplate("ADMIN_ADD_STUDENT_TO_SUBJECT", new AdminAddUserToSubject())

                // TEACHER
                .addRequestTemplate("TEACHER_MAIN_PAGE", new TeacherMainPage())
                .addRequestTemplate("TEACHER_LOAD_SUBJECT", new TeacherLoadSubject());

    }

    /**
     * Adds the template to the handler.
     *
     * @param name     name of the request
     * @param template instance of a class that implements {@link IRequest}
     */
    public ClientNetworkService addRequestTemplate(String name, IRequest template) {
        requestsTemplates.put(name, template);
        return this;
    }

    public void start() {
        networkService.submit(() -> {
            Thread.currentThread().setName("NetworkService");
            group = new NioEventLoopGroup();
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new DelimiterBasedFrameDecoder(2097152, Delimiters.lineDelimiter()));
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            pipeline.addLast(new SimpleChannelInboundHandler<String>() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    Gui.getInstance().setScene("loginScreen");
                                    ClientInstance.getInstance().getNetworkService().setCtx(ctx);

                                    Platform.runLater(() -> {
                                        Gui.getInstance().getLoginController().login_signin_button.setOnAction(event -> sendRequest(
                                                "AUTHENTICATION",
                                                ctx,
                                                new String[]{Gui.getInstance().getLoginController().login_username_field.getText(), Gui.getInstance().getLoginController().login_password_field.getText()}
                                        ));
                                    });
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    logger.log(MESSAGE, msg);
                                    onMessage(ctx, msg);
                                }
                            });
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            cause.printStackTrace();
                            assert false : cause.getMessage();
                        }
                    });

            ChannelFuture future = null;

            // looping until connection is established

            String text = (String) ResourceClientProperties.getInstance().getObject("host");
            int port = Integer.parseInt((String) ResourceClientProperties.getInstance().getObject("port"));
            do {
                future = clientBootstrap.connect(text, port);
                future.awaitUninterruptibly();
            } while (!future.isSuccess());
        });
    }

    public void stop() {
        group.shutdownGracefully();
        networkService.shutdownNow();
    }

    private void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /*                                          =========PacketHandler======
    /*========================================================================================================================================*/

    public void onMessage(ChannelHandlerContext channelHandlerContext, String message) {
        try {
            Packet receivedPacket = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(message, Packet.class);
            receivedPacket.setChannelHandlerContext(channelHandlerContext);

            // check if the packet is an answer to an inbound request
            if (receivedPacket.getRequestStatus()) {
                for (Packet packet : waitingOutboundPackets) {
                    if (packet.getRequestId() == receivedPacket.getRequestId()) {
                        requestsTemplates.get(packet.getRequestName()).onAnswer(receivedPacket);
                        waitingOutboundPackets.remove(packet);
                        return;
                    }
                }
                // throw error: no request found
                //logger.error("No request found with id provided.");
            }

            // packet is a new request at this point
            if (requestsTemplates.get(receivedPacket.getRequestName()) == null) {
                // throw error: no request with this name found
                //logger.error("Invalid request name: " + receivedPacket.getRequestName());
                return;
            }

            requestsTemplates.get(receivedPacket.getRequestName()).onIncomingRequest(receivedPacket);

        } catch (JsonProcessingException e) {
            //logger.error(e.getMessage());
        }
    }


    /**
     * Instructs the service to send a request on the next network-loop using the specified {@link ChannelHandlerContext}.
     *
     * @param name   of the request.
     * @param ctx    {@link ChannelHandlerContext} that the request wished to be sent.
     * @param params any parameters that the Request might need.
     * @throws IllegalArgumentException if the request name is invalid.
     */
    public void sendRequest(String name, ChannelHandlerContext ctx, Object[] params) throws IllegalArgumentException {
        if (requestsTemplates.get(name) == null)
            throw new IllegalArgumentException("No request template found with passed name \"" + name + "\"");
        Packet packet = new Packet(name, ctx);
        requestsTemplates.get(name).onNewRequest(packet, params);
        waitingOutboundPackets.add(packet);
    }

    /**
     * Instructs the service to send a request on the next network-loop using the known {@link ChannelHandlerContext}.
     *
     * @param name   of the request.
     * @param params any parameters that the Request might need.
     * @throws IllegalArgumentException if the request name is invalid.
     */
    public void sendRequest(String name, Object[] params) throws IllegalArgumentException {
        if (requestsTemplates.get(name) == null)
            throw new IllegalArgumentException("No request template found with passed name \"" + name + "\"");
        Packet packet = new Packet(name, ctx);
        requestsTemplates.get(name).onNewRequest(packet, params);
        waitingOutboundPackets.add(packet);
    }

    public interface IRequest {

        /**
         * Called when {@link #sendRequest(String, ChannelHandlerContext, Object[])} is called.
         */
        void onNewRequest(Packet packet, Object[] params);

        void onAnswer(Packet packet);

        default void onIncomingRequest(Packet packet) {
        }
    }

    public static class Authentication implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("username", params[0].toString())
                    .put("password", params[1].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            if (packet.getRequestContent().get("message").asText().equals(Packet.PACKET_CODES.SUCCESS.name())) {
                ClientInstance.getInstance().getNetworkService().sendRequest("GET_SELF_USER", null);
                ClientInstance.getInstance().getNetworkService().sendRequest("GET_ARTICLES", null);
                Platform.runLater(() -> Gui.getInstance().setScene("loadingScreen"));
                return;
            }
            Platform.runLater(() -> {
                Gui.getInstance().getLoginController().login_info_label
                        .setText(ResourceClientMessages
                                .getObjectAsString((packet
                                        .getRequestContent().get("message").asText())));
            });
        }
    }

    public static class GetSelfUser implements IRequest {


        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            if (packet.getRequestContent().get("message") != null) {
                // if there is an error exit
                ClientInstance.getInstance().stopApplication();
            }

            String userJson = packet.getRequestContent().toPrettyString();
            try {
                ClientInstance.getInstance().setSelfUser(new ObjectMapper().readValue(userJson, User.class));
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                ClientInstance.getInstance().stopApplication();
            }

            Platform.runLater(() -> {
                Gui.getInstance().setScene("dashboardScreen");
            });
        }
    }

    public static class GetMainPageArticles implements IRequest {
        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            JsonNode node = packet.getRequestContent().get("articles");
            List<Article> mainPageArticles = Gui.getInstance().getMainPageArticles();
            for (final JsonNode tempNode : node) {
                try {
                    Article e = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).treeToValue(tempNode, Article.class);
                    mainPageArticles.add(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            Gui.getInstance().loadGuiBridge();
        }

        @Override
        public void onIncomingRequest(Packet packet) {

        }
    }

    public static class AdminGetUsers implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            JsonNode users = packet.getRequestContent().get("users");
            Gui.getInstance().callbackAdminGetUsers(users);
        }
    }

    public static class AdminAddUsers implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("username", params[0].toString())
                    .put("password", params[1].toString())
                    .put("firstName", params[2].toString())
                    .put("lastName", params[3].toString())
                    .put("type", params[4].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            if (packet.getRequestContent().get("message").asText().equals("SUCCESS")) {
                Gui.getInstance().callbackAdminAddUsers();
            }
        }
    }

    public static class AdminDeleteUsers implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("id", params[0].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().callbackAdminDeleteUsers();
        }
    }

    public static class AdminAddSubjects implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("name", params[0].toString())
                    .put("teacher", params[1].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().callbackAdminAddSubjects();
        }
    }

    public static class AdminGetSubjects implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            JsonNode subjects = packet.getRequestContent().get("subjects");
            Gui.getInstance().callbackAdminGetSubjects(subjects);
        }
    }

    private static class AdminDeleteSubjects implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("id", params[0].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().callbackAdminDeleteSubjects();
        }
    }

    private static class AdminGetTeachers implements IRequest {
        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            JsonNode users = packet.getRequestContent().get("teachers");
            Gui.getInstance().callbackAdminGetTeachers(users);
        }
    }

    public static class AdminGetStudents implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("subject", params[0].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            JsonNode users = packet.getRequestContent().get("students");
            Gui.getInstance().callbackAdminGetStudents(users);
        }

    }

    public static class AdminAddUserToSubject implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("subjectId", params[1].toString())
                    .put("userId", params[0].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().requestAdminGetSubjects();
        }
    }

    public static class TeacherMainPage implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().callbackTeacherMainPage(packet.getRequestContent().get("subjects"));
        }
    }

    public static class TeacherLoadSubject implements IRequest {

        @Override
        public void onNewRequest(Packet packet, Object[] params) {
            packet.setRequestContent(new ObjectMapper().createObjectNode()
                    .put("id", params[0].toString())
            );
            try {
                packet.sendThis(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onAnswer(Packet packet) {
            Gui.getInstance().callbackTeacherLoadSubject(packet.getRequestContent());
        }
    }
}

