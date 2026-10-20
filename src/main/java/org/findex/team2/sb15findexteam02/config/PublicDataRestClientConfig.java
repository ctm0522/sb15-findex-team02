package org.findex.team2.sb15findexteam02.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PublicDataRestClientConfig {
  // 생성
  @Bean
  public RestClient restClient() {
    return RestClient.builder()
        .requestFactory(new HttpComponentsClientHttpRequestFactory())
        .baseUrl("https://apis.data.go.kr")
        .build();
  }
}
