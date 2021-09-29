package cn.netbuffer.oauth2.demo.server.service;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sa-Token OAuth2.0 整合实现
 */
@Slf4j
@Component
public class SaOAuth2TemplateImpl extends SaOAuth2Template {

    // 根据 id 获取 Client 信息 
    @Override
    public SaClientModel getClientModel(String clientId) {
        log.debug("getClientModel clientid={}", clientId);
        // 此为模拟数据，真实环境需要从数据库查询 
        if ("10001".equals(clientId)) {
            return new SaClientModel()
                    .setClientId("10001")
                    .setClientSecret("aaaa-bbbb-cccc-dddd-eeee")
                    .setAllowUrl("*")
                    .setContractScope("userinfo");
        }
        return null;
    }

    // 根据ClientId 和 LoginId 获取openid 
    @Override
    public String getOpenid(String clientId, Object loginId) {
        log.debug("getOpenid clientid={},loginid={}", clientId, loginId);
        // 此为模拟数据，真实环境需要从数据库查询 
        return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
    }

}