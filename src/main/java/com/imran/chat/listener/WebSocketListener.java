package com.imran.chat.listener;

import com.imran.chat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author imran ahmad
 */
@Component
public class WebSocketListener {

    private static final Logger logger= LoggerFactory.getLogger(WebSocketListener.class);

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("received new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListerner(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String userName=(String)headerAccessor.getSessionAttributes().get("username");
        if(null!=userName) {
            logger.info("User disconnected: "+userName);

            ChatMessage message=new ChatMessage();
            message.setType(ChatMessage.MessageType.LEAVE);
            message.setSender(userName);

            sendingOperations.convertAndSend("/topic/public",message);
        }
    }
}
