package net.dima_community.CommunityProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	/**
	 *STOMP 활성화 
	 */
	
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // WebSocket 지원이 없는 브라우저에서도 작동하도록 SockJS 사용 
    }
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//    	config.setPathMatcher(new AntPathMatcher(".")); // URL을 / -> .으로
    	// 메시지 구독 url 
    	config.setApplicationDestinationPrefixes("/app"); // 메시지의 header가 /app으로 시작하면, @Controller 클래스의 @MessageMapping 매서드로 라우
    	 // 메시지 발행 url sub(topic), pub 를 위해 메시지 브로커 사용 
    	config.enableStompBrokerRelay("/topic","/queue","/exchange", "/amq/queue")
		    	.setRelayHost("localhost")
		        .setRelayPort(61613)  // STOMP 포트 설정
		        .setClientLogin("guest")
		        .setClientPasscode("guest");
    	;
		        
    }
    

    
}