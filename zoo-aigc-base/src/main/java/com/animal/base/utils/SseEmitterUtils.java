package com.animal.base.utils;

import com.animal.product.common.LocalCache;
import com.zoo.friend.entity.AI.chat.ChatGPTMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 咏鹅
 * @version 1.0
 * @description TODO
 * @date 2023/8/31 17:48
 */
@Slf4j
public class SseEmitterUtils {

    public SseEmitter createSseEmitter(String sid) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.onTimeout(()->{
            log.info("sse连接超时...................");
        });
        sseEmitter.onCompletion(()->{
            LocalCache.CACHE.remove(sid);
        });
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常,{}", sid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(sid)
                                .name("发生异常！")
                                .data(ChatGPTMessage.Party().setContent("发生异常请重试！").partyRun())
                                .reconnectTime(3000));
                        LocalCache.CACHE.put(sid, sseEmitter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        return sseEmitter;
    }
}
