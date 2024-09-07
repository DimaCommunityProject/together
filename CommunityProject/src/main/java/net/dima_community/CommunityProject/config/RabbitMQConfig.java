package net.dima_community.CommunityProject.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String CHAT_QUEUE_NAME = "chat.queue";
    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "room.*";
    
    /**
     * 1. 지정된 큐 이름으로 Queu 빈을 생성
     *  chat.queue
     * @return Queue
     */
    @Bean
    public Queue queue() { 
        return new Queue(CHAT_QUEUE_NAME, true, false, false); // true로 설정하여 durable하게 유지
    }
    
    /**
     * 2. 지정된 exchage이름으로 TopicExchage 빈을 생성
     * chat.exchage
     * @return TopicExchange
     */
    @Bean
    public TopicExchange exchange() { 
        return new TopicExchange(CHAT_EXCHANGE_NAME);  //, true, false
    }

    
    /**
     * 3. Exchange와 Queue를 바인딩하고 라우팅 키를 이용하여 Binding 빈을 생성 
     *  room.* 라는 이름으로 바인딩 구
     * @param queue
     * @param exchange
     * @return BindingBuilder
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
        		.bind(queue)
        		.to(exchange)
        		.with(ROUTING_KEY);
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter()); // 메시지 변환기 설정
        factory.setAutoStartup(false); // 자동으로 시작하지 않도록 설정
        return factory;
    }

    /**
     * 4. RabbitMQ와의 연결을 위한 CachingConnectionFactory빈을 생성하여 반환
     * 
     * @return
     */
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }
    
    /**
     * 5. 구성한 ConnectionFactory, MessageConverter를 통해 템플릿 구성
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정 
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());  // 메시지 변환기 설정
        return rabbitTemplate;
    }
    
    /**
     * 6. 메시지를 전송하고 수신하기 위한 JSON 타입으로 메시지 변경
     * Jackson2JsonMessageConverter를 사용하여 메시지 변환 수행 
     * @return
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    
    

    
}