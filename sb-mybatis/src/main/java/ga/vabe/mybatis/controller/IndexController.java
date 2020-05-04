package ga.vabe.mybatis.controller;

import ga.vabe.common.IdGenerator;
import ga.vabe.mybatis.entity.TCbmTaskpool;
import ga.vabe.mybatis.service.ITCbmTaskpoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequestMapping("/")
public class IndexController extends AppController {

    @Autowired
    private ITCbmTaskpoolService taskpoolService;

    // 集合容量
    int capacity = 256;
    List<TCbmTaskpool> cacheList = new ArrayList<>(capacity);

    // 开启一个批量插入线程，将需要入库的数据临时保存在 cacheList 中
    {
        Thread batchInsertThread = new Thread(() -> {
            for(;;) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 双检查锁
                if (!CollectionUtils.isEmpty(cacheList)) {
                    synchronized (cacheList) {
                        if (!CollectionUtils.isEmpty(cacheList)) {
                            // 将 cacheList 赋值给新对象，避免在批量保存时长时间锁住该对象造成系统无法响应新增接口
                            List<TCbmTaskpool> tempList = new ArrayList<>(cacheList);
                            // 1.5倍实际容量，尽量避免集合触发扩容操作
                            capacity = tempList.size() + (tempList.size() >> 1) + 7;
                            cacheList = new ArrayList<>(capacity);
                            // mybatis plus saveBatch 方法中默认 batchSize 为 1000，其他框架在做批量操作时注意集合大小，过大的集合有可能导致不成功
                            // 注意：如果批量插入操作多次非常耗时，会使得线程轮询变长从而导致 cacheList 越来越大（越来越大导致轮询时间越来越长，这是个恶性循环）
                            // 这时候可以通过线程池去处理批量保存（但根本解决方法是限流，因为服务器处理不了这么多数据）
                            taskpoolService.saveBatch(tempList);
                            log.info("batch saves size: " + tempList.size());
                            tempList.clear();
                        }
                    }
                }
            }
        });
        batchInsertThread.setName("batch-save-exec");
        batchInsertThread.setDaemon(true);
        batchInsertThread.start();
    }

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        long begin = System.currentTimeMillis();
        save(instance("abe"));
        long elapse = System.currentTimeMillis() - begin;
        return "集合容量: " + capacity + "\n" +
                "当前并发:" + context.guest() + "\n" +
                "并发边界:" + context.guestBound + "\n" +
                "插入用时: " + elapse + "ms\n";
    }

    @RequestMapping(value = {"/add/{who}", "/add"})
    public String add(@PathVariable(required = false) String who) {
        save(instance(who == null ? "none" : who));
        return "ok";
    }

    public TCbmTaskpool instance(String who) {
        TCbmTaskpool pool = new TCbmTaskpool();
        pool.setFPid(IdGenerator.instance().hexId());
        pool.setFBelongtoPid(randomValue());
        pool.setFBranch(randomValue());
        pool.setFCity(randomValue());
        pool.setFProvince(randomValue());
        pool.setFOrgRoute(randomValue());
        pool.setFWorkteam("what's your problem ???");
        pool.setFCreator(who);
        pool.setFCreateTime(String.valueOf(System.currentTimeMillis()));
        pool.setFSysFlag("1");
        return pool;
    }

    public boolean save(TCbmTaskpool tp) {
        if (context.overGuest()) {
            synchronized (cacheList) {
                cacheList.add(tp);
            }
        } else {
            return taskpoolService.save(tp);
        }
        return true;
    }

    public String randomValue() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(100000000L, 1000000000L));
    }

}
