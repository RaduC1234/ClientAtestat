package me.raducapatina.client.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.raducapatina.client.data.User;
import me.raducapatina.client.gui.Gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The request system works like REST but without using HTTP. The procedure is
 * make up of one "question" and one "answer". Works the same for outbound and inbound
 * requestsTemplates.
 *
 * @author Radu
 */
public class ClientRequestHandler {

    private Map<String, RequestTemplate> requestsTemplates = new HashMap();
    private List<Packet> waitingOutboundPackets = new ArrayList<>();
    private ChannelHandlerContext ctx;

    public ClientRequestHandler() {

    }

    /**
     * Adds the template to the handler.
     *
     * @param name     name of the request
     * @param template instance of a class that implements {@link RequestTemplate}
     * @return
     */
    public ClientRequestHandler addRequestTemplate(String name, RequestTemplate template) {
        requestsTemplates.put(name, template);
        return this;
    }

    /**
     * @param channelHandlerContext
     * @param message
     */
    public void onMessage(ChannelHandlerContext channelHandlerContext, String message) {
        try {
            Packet receivedPacket = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(message, Packet.class);
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
            }

            // packet is a new request at this point
            if (requestsTemplates.get(receivedPacket.getRequestName()) == null) {
                // throw error: no request with this name found
                return;
            }

            requestsTemplates.get(receivedPacket.getRequestName()).onIncomingRequest(receivedPacket);

        } catch (JsonProcessingException e) {
            assert false : e.getMessage();
        }
    }

    public void sendRequest(String name, ChannelHandlerContext ctx, Object[] params) throws IllegalArgumentException {
        if (requestsTemplates.get(name) == null)
            throw new IllegalArgumentException("No request template found with passed name");
        Packet packet = new Packet(name, ctx);
        requestsTemplates.get(name).onNewRequest(packet, params);
        waitingOutboundPackets.add(packet);
    }

    public void sendRequest(String name, Object[] params) throws IllegalArgumentException {
        if (requestsTemplates.get(name) == null)
            throw new IllegalArgumentException("No request template found with passed name");
        Packet packet = new Packet(name, ctx);
        requestsTemplates.get(name).onNewRequest(packet, params);
        waitingOutboundPackets.add(packet);
    }

    public ClientRequestHandler setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        return this;
    }


    public interface RequestTemplate {

        void onNewRequest(Packet packet, Object[] params);

        void onAnswer(Packet packet);

        void onIncomingRequest(Packet packet);
    }


    public static class AuthenticationTemplate implements RequestTemplate {

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
                ClientInstance.getInstance().getRequestHandler().sendRequest("GET_SELF_USER", null);
                Platform.runLater(() -> Gui.getInstance().setScene("loadingScreen"));
                return;
            }
            Platform.runLater(() -> {
                Gui.getInstance().getLoginController().login_info_label.setText(packet.getRequestContent().get("message").asText());
            });
        }

        @Override
        public void onIncomingRequest(Packet packet) {

        }
    }

    public static class GetSelfUser implements RequestTemplate {
        private User selfUser;

        public GetSelfUser(User selfUser) {
            this.selfUser = selfUser;
        }

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
                this.selfUser = new ObjectMapper().readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                ClientInstance.getInstance().stopApplication();
            }

            WebEngine engine = Gui.getInstance().getMainModule().getBrowser().getEngine();

            switch (selfUser.getType()) {

                case STUDENT -> {
                }
                case TEACHER -> {
                }
                case ADMIN -> {
                }
                case UNKNOWN -> {
                }
            }

            Platform.runLater(() -> {
                Gui.getInstance().setScene("dashboardScreen");
            });
        }

        @Override
        public void onIncomingRequest(Packet packet) {

        }
    }
}
