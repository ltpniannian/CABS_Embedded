package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.model.Observation;
import com.swe.gateway.util.WebSocketSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RealTimeHandler implements WebSocketHandler {
    @Autowired
    private ConcurrentHashMap<String, WebSocketSender> senderMap;
    //用了一个Map存储传感器的实时数据，RRALTIME_DATA只需要存每个设备的每个观测属性的最新一条数据就可以了
    //key为sensorName+'_'+obsPropName组成的字符串
    //前端点击发送一个传感器的设备id和一个观测属性id，然后去获取传感器的最新一条数据
    //可以用http://coolaf.com/tool/chattest测试ws的接口0
     public static final ConcurrentHashMap<String, Observation> REALTIME_DATA = new ConcurrentHashMap<>();
    //这个变量去判断是否继续向当前session持续发送消息
    //不用interrupt()是因为在调用isinterrupted()判断之后标志位又会被置为true，另外这样可以控制向多个客户端的发送情况
    private static final ConcurrentHashMap<String, Integer> SESSION_SIGNAL = new ConcurrentHashMap<>();
    //这个map用于记录当前Session的send线程，否则会出现两个线程同时发送的情况
    private static final ConcurrentHashMap<String, SendThread> SESSION_THREAD = new ConcurrentHashMap<>();
    private static final Logger logger = LogManager.getLogger(RealTimeHandler.class.getName());



    @Override
    public Mono<Void> handle(WebSocketSession session) {

        String sessionid = session.getId();
//        模拟数据，仅供测试
//        REALTIME_DATA.put("NBIOT-001_土壤温度", new Observation(1, 1, 2, 3, "11.5", Date.from(Instant.now())));
//        REALTIME_DATA.put("NBIOT-001_土壤湿度", new Observation(1, 2, 2, 3, "22.5", Date.from(Instant.now())));
//        REALTIME_DATA.put("NBIOT-002_土壤湿度", new Observation(2, 1, 2, 3, "33.5", Date.from(Instant.now())));

        //获取到当前session的send函数的fluxsink对象并保存到senderMap里面
        Mono<Void> output = session.
                send(Flux.create(sink ->
                        senderMap.put(sessionid, new WebSocketSender(session, sink))));

        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(message -> {
                    String info = "接收到客户端[" + sessionid + "]发送的数据：" + message;
                    logger.info(info);
                    //前端传回一个JSON对象，格式为{signal:0,sensorName:"NBIOT-001",obsPropName:"土壤温度"}
                    //接收到signal=0表示客户端暂停接收数据，signal=t表示前端希望每隔ts接收到服务端发送的数据
                    JSONObject jsonObject = JSONObject.parseObject(message);
                    int signal = (int) jsonObject.get("signal");
                    String sensorName_obsPropName = jsonObject.getString("sensorName") + "_" + jsonObject.getString("obsPropName");
                    if (signal <= 0) {
                        SESSION_SIGNAL.put(sessionid, 0);
                        logger.info("停止向客户端[" + sessionid + "]发送数据");
                        //清除发送线程
                        if(SESSION_THREAD.get(sessionid)!=null){
                            logger.info("停止发送线程"+SESSION_THREAD.get(sessionid).toString());
                            SESSION_THREAD.get(sessionid).flag=false;
                        }
                    } else {
                        SESSION_SIGNAL.put(sessionid, 1);
                        SendThread sendThread=new SendThread(sessionid,sensorName_obsPropName,signal);
                        sendThread.start();
                        SESSION_THREAD.put(sessionid,sendThread);
                    }
                    return message;
                }).then();
        /**
         * Mono.zip() 会将多个 Mono 合并为一个新的 Mono，任何一个 Mono 产生 error 或 complete 都会导致合并后的 Mono
         * 也随之产生 error 或 complete，此时其它的 Mono 则会被执行取消操作。
         * 这里由于是响应式，好像没有定义onopen和onclose的函数，全部都转发到了handle里面
         */

        return Mono.zip(output, input)
                .doOnSubscribe(s -> logger.info("客户端[" + sessionid + "]建立连接"))
                .doOnError(s -> {
                    logger.info("客户端[" + sessionid + "]发生错误" + s.getLocalizedMessage());
                    SESSION_SIGNAL.remove(sessionid);
                    senderMap.remove(sessionid);
                    session.close();
                })
                .doOnSuccess(s -> {
                    SESSION_SIGNAL.remove(sessionid);
                    senderMap.remove(sessionid);
                    session.close();
                    logger.info("客户端[" + sessionid + "]关闭连接");
                }).then();

    }

    private class SendThread extends Thread{
        public volatile boolean flag=true;

        public SendThread(String sessionid, String sensorName_obsPropName, int signal) {
            this.sessionid = sessionid;
            this.sensorName_obsPropName = sensorName_obsPropName;
            this.signal = signal;
        }

        private String sessionid;
        private String sensorName_obsPropName;
        private int signal;

        @Override
        public void run(){
            while (flag&&SESSION_SIGNAL.get(sessionid) != null && SESSION_SIGNAL.get(sessionid) == 1) {
                Observation latestObs = REALTIME_DATA.get(sensorName_obsPropName);
                //模拟实时数据,仅供前端测试
                latestObs = new Observation(2, 1, 2, 3, new DecimalFormat("0.00").format(21), Date.from(Instant.now()));

                if (latestObs != null) {
                    logger.info("服务端向客户端[" + sessionid + "]发送实时数据: " + latestObs.toString());
                    //通过senderMap获取到当前session的sendsink发送数据
                    senderMap.get(sessionid).sendData(JSONObject.toJSON(latestObs).toString());
                } else {
                    logger.info("没有可用的实时数据 传感器_观测能力ID:" + sensorName_obsPropName);
                    SESSION_SIGNAL.put(sessionid, 0);
                    return;
                }
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}




