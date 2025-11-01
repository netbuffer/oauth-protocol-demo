package cn.netbuffer.oauth2.demo.server;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
        log.info("\nSa-Token-OAuth2 Server端启动成功，配置如下：");
        log.info("{}", SaOAuth2Manager.getServerConfig());
    }

}