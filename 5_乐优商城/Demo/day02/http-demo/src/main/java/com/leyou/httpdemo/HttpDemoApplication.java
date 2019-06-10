package com.leyou.httpdemo;

		import org.apache.http.impl.client.CloseableHttpClient;
		import org.apache.http.impl.client.HttpClients;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.context.annotation.Bean;
		import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
		import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class HttpDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HttpDemoApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		// 默认的RestTemplate，底层是走JDK的URLConnection方式。
		// 如果想用另外两个，引入依赖，传参即可。
		return new RestTemplate();
	}
}
