package ga.vabe.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CalculateOperationContext {

    private final Map<Integer, CalculateStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    private void initStrategies(List<CalculateStrategy> strategyList) {
        strategyMap.clear();
        for (int i = 0; i < strategyList.size(); i++) {
            CalculateStrategy calculateStrategy = strategyList.get(i);
            log.info("{} : {}", i, calculateStrategy.desc());
            strategyMap.put(i, calculateStrategy);
        }
    }

    public Optional<CalculateStrategy> get(int type) {
        return Optional.ofNullable(strategyMap.get(type));
    }

}