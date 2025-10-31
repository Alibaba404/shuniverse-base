package cn.shuniverse.base;

import cn.shuniverse.base.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * Created by 蛮小满Sama at 2025-10-31 15:37
 *
 * @author 蛮小满Sama
 * @description
 */
@Slf4j
public class IPTest {

    @Test
    public void test1() {
        // 韩国|0|首尔|首尔|0
        // 中国|0|四川省|成都市|移动
        log.info("ip: {}", IPUtil.ipRegion("183.222.241.41"));
    }
}
