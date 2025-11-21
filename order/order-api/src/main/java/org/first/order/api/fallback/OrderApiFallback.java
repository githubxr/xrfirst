package org.first.order.api.fallback;

import org.first.order.api.OrderApi;

//@Slf4j
public class OrderApiFallback implements OrderApi {

    @Setter
    private Throwable cause;

    //@Override
    //public void sendSysAnnouncement(MessageDTO message) {
    //    log.error("发送消息失败 {}", cause);
    //}
    @Override
    public String hello() {
        return "";
    }
}


