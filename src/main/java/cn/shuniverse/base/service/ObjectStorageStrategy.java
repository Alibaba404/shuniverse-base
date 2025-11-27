package cn.shuniverse.base.service;

import cn.shuniverse.base.service.impl.DefaultFileStorageStrategyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by 蛮小满Sama at 2025-11-24 10:15
 *
 * @author 蛮小满Sama
 * @description 对象存储策略
 */
@Component
public class ObjectStorageStrategy {

    private final DefaultFileStorageStrategyServiceImpl defaultFileStorageStrategyService;
    private Map<String, IObjectStorageStrategyService> strategyServiceMap;

    public ObjectStorageStrategy(DefaultFileStorageStrategyServiceImpl defaultFileStorageStrategyService) {
        this.defaultFileStorageStrategyService = defaultFileStorageStrategyService;
    }


    @Autowired
    public void setStrategyServiceMap(List<IObjectStorageStrategyService> iObjectStorageStrategyServices) {
        this.strategyServiceMap = iObjectStorageStrategyServices.stream()
                .collect(Collectors.toMap(IObjectStorageStrategyService::sport, Function.identity()));
    }

    /**
     * 获取对象存储服务
     *
     * @param fileStorageClassifyString 对象存储分类
     * @return 对象存储服务
     */
    public IObjectStorageStrategyService peek(String fileStorageClassifyString) {
        IObjectStorageStrategyService iObjectStorageStrategyService = strategyServiceMap.get(fileStorageClassifyString);
        if (iObjectStorageStrategyService != null) {
            return iObjectStorageStrategyService;
        }
        return defaultFileStorageStrategyService;
    }
}
