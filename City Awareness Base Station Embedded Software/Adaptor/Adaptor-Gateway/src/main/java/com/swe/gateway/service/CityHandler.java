package com.swe.gateway.service;


import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.CityRepository;
import com.swe.gateway.model.City;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * 城市处理器
 *
 *
 * Mono 和 Flux 适用于两个场景，即：
 Mono：实现发布者，并返回 0 或 1 个元素，即单对象。
 Flux：实现发布者，并返回 N 个元素，即 List 列表对象。
 有人会问，这为啥不直接返回对象，比如返回 City/Long/List。
 原因是，直接使用 Flux 和 Mono 是非阻塞写法，相当于回调方式。
 利用函数式可以减少了回调，因此会看不到相关接口。这恰恰是 WebFlux 的好处：集合了非阻塞 + 异步
 */
@Component
public class CityHandler {

    private static Logger logger = LogManager.getLogger(CityHandler.class.getName());

    /**
     * 数据操作的dao层的bean
     */
    private final CityRepository cityRepository;

    /**
     * 通过构造器注入初始化属性cityRepository
     * @param cityRepository
     */
    @Autowired
    public CityHandler(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * 保存城市数据的处理方法
     * @param city
     * @return
     */
    public Mono<Long> save(City city) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(cityRepository.save(city)));
    }

    /**
     * 通过城市id查询城市的处理方法
     * @param id
     * @return
     */
    public Mono<City> findCityById(Long id) {
        return Mono.justOrEmpty(cityRepository.findCityById(id));
    }

    /**
     * 查询所有城市数据
     * @return
     */
    public Flux<City> findAllCity() {
        return Flux.fromIterable(cityRepository.findAll());
    }

    /**
     * 修改城市数据
     * @param city
     * @return
     */
    public Mono<Long> modifyCity(City city) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(cityRepository.updateCity(city)));
    }

    /**
     * 根据城市id删除城市数据
     * @param id
     * @return
     */
    public Mono<Long> deleteCity(Long id) {
        return Mono.create(cityMonoSink -> cityMonoSink.success(cityRepository.deleteCity(id)));
    }

    //查询出一个 city 集合 并将这个集合 序列化到响应体中
    public Mono<ServerResponse> listCity(ServerRequest request) {
        Mono <City> city = request.bodyToMono(City.class);
        return city.flatMap(s-> {
                    s.setCityName("wuhan");
                    logger.info(s.getCityName());
                    cityRepository.save(s);
                    Flux<City> cityFlux = findAllCity();
                    return ServerResponse.ok().contentType(APPLICATION_JSON).body(cityFlux, City.class);
                }
        );
    }

    //测试数据：{"id":1,"provinceId":2,"cityName":"111","description":{text:"test"}}
    public Mono<ServerResponse> test(ServerRequest request) {
        Mono <String> str = request.bodyToMono(String.class);
        return str.flatMap(s-> {
            logger.info(s);
            JSONObject json = JSONObject.parseObject(s);//能够解析整个json串
            logger.info(json.getJSONObject("description").getString("text"));
            return ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(s),String.class);
        });
    }

    public Mono<ServerResponse> testWebClient(ServerRequest request) {
        final WebClient webClient =WebClient.builder().baseUrl("http://localhost:8764").build();
        Mono<String> resp=webClient.post()
                .uri("/login/userLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{username:\"cbw\",password:\"cbw\"}"),String.class)
                .retrieve().bodyToMono(String.class);
        Mono<String> resp1=webClient.get()
                .uri("/admin/info")
                .header("token","swe-eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIzIiwic3ViIjoiY2J3IiwiaWF0IjoxNTc3OTMxMzYxLCJpc3MiOiJjYnciLCJhdXRob3JpdGllcyI6Ilt7XCJhdXRob3JpdHlcIjpcIlJPTEVfVVNFUlwifV0iLCJleHAiOjE1Nzc5Mzg1NjF9.PPoRrLh_bEpyLh6YEdGdxECAFcrcpn6wT-uzsYeHvb_bwPUdh1WSAK3moXAxrXxtSZId7WDkPylyoxC8cmeRQA")
                .retrieve().bodyToMono(String.class);
        //通过订阅的方式异步获取响应结果
        resp.subscribe(s->logger.info(s));
        resp.subscribe(s->logger.info(s));
        resp1.subscribe(s-> logger.info(s));

        //block是阻塞获取方式，能够在当前线程拿到响应值,并且控制请求顺序逻辑 ====> 异步并不代表并发，block无法实现并发执行
        // 不要为每一个响应单独添加block，这样会影响性能，好的处理方式是避免单独阻塞每个响应，而是组合响应结果zip
       /* logger.info("test");
        logger.info(resp.block());
        logger.info(resp1.block());*/

       /* Map map = Mono.zip(resp, resp1, (result1, result2) -> {
            Map arrayList = new HashMap<>();
            arrayList.put("result1", result1);
            arrayList.put("result2", result2);
            return arrayList;
        }).block();*/

        return ServerResponse.ok().build();
    }

}


