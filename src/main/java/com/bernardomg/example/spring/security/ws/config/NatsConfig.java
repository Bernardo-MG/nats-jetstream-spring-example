
package com.bernardomg.example.spring.security.ws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bernardomg.example.spring.security.ws.jetstream.init.JetStreamInitializer;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.Options;

@Configuration
@EnableConfigurationProperties(NatsProperties.class)
public class NatsConfig {

    /**
     * Class logger.
     */
    private static final Logger log = LoggerFactory.getLogger(NatsConfig.class);

    @Bean
    public JetStream jetStream(final Connection connection) throws Exception {
        return connection.jetStream();
    }

    @Bean(initMethod = "setup")
    public JetStreamInitializer jetStreamInitializer(final JetStreamManagement jsm) {
        return new JetStreamInitializer(jsm);
    }

    @Bean
    public JetStreamManagement jetStreamManagement(final Connection connection) throws Exception {
        return connection.jetStreamManagement();
    }

    @Bean
    public Connection natsConnection(final NatsProperties natsProperties) throws Exception {
        final Options options;

        log.info("Connecting to NATS at {}", natsProperties.url());
        options = new Options.Builder().server(natsProperties.url())
            .userInfo(natsProperties.username(), natsProperties.password())
            .build();

        return Nats.connect(options);
    }

}
