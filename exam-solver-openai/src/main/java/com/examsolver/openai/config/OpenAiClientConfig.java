package com.examsolver.openai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
@RequiredArgsConstructor
public class OpenAiClientConfig {

    private final OpenAiProperties properties;

    @Bean
    public WebClient openAiWebClient() {
        final Duration connect = properties.getTimeout().getConnect();
        final Duration read = properties.getTimeout().getRead();
        final Duration write = properties.getTimeout().getWrite();
        final Duration httpResp = properties.getTimeout().getText();

        final HttpClient http = HttpClient.create()
                .compress(true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connect.toMillis())
                .responseTimeout(httpResp)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(read.toMillis(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(write.toMillis(), TimeUnit.MILLISECONDS))
                );

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(http))
                .exchangeStrategies(strategies)
                .defaultHeaders(h -> {
                    h.setBearerAuth(properties.getApiKey());
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.setAccept(List.of(MediaType.APPLICATION_JSON));
                    h.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
                })
                .build();
    }
}
