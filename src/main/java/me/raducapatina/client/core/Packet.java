package me.raducapatina.client.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

@JsonIgnoreProperties(value = {"channelHandlerContext"})
public class Packet {

    private String requestName;
    private boolean requestStatus;
    private long requestId;
    private JsonNode requestContent;
    private ChannelHandlerContext channelHandlerContext;

    public Packet() {
    }

    public Packet(String name, ChannelHandlerContext channelHandlerContext) {
        this.requestName = name;
        this.channelHandlerContext = channelHandlerContext;
        this.requestStatus = false;
        this.requestId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public ChannelFuture sendThis() throws Exception {
        return this.channelHandlerContext.writeAndFlush(this.toJson() + "\r\n");
    }

    public ChannelFuture sendError(PACKET_CODES code) {
        return this.channelHandlerContext.writeAndFlush("{\"requestName\": \"" + requestName + "\", \"requestStatus\": true, \"requestId\" : " + requestId + ",\"responseStats\" : 0, \"requestContent\" : {\"message\" : \"" + code.ordinal() + "\"}}\r\n");
    }

    public ChannelFuture sendSuccess() {
        return this.channelHandlerContext.writeAndFlush("{\"requestName\": \"" + requestName + "\", \"requestStatus\": true, \"requestId\" : " + requestId + ",\"responseStats\" : 1, \"requestContent\" : {\"message\" : \"" + PACKET_CODES.SUCCESS.ordinal() + "\"}}\r\n");
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

    public Packet setRequestName(String requestName) {
        this.requestName = requestName;
        return this;
    }

    public Packet setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
        return this;
    }

    public Packet setRequestId(long requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getRequestName() {
        return requestName;
    }

    public boolean getRequestStatus() {
        return requestStatus;
    }

    public long getRequestId() {
        return requestId;
    }

    public JsonNode getRequestContent() {
        return requestContent;
    }

    public Packet setRequestContent(JsonNode requestContent) {
        this.requestContent = requestContent;
        return this;
    }

    public Packet setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
        return this;
    }

    public enum PACKET_CODES {

        SUCCESS,
        ERROR,

        UNKNOWN_REQUEST,
        REQUEST_SYNTAX_ERROR,

        USER_NOT_FOUND,
        INVALID_PASSWORD,
    }
}
