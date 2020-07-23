
package cn.csdb.msis.monitor;

import java.util.Date;

/**
 * 监控器的抽象类
 *
 * @author CXY
 */
public abstract class AbstractMonitor implements Monitor {

    /**
     * 获取当前的时间，并以yyyy-MM-dd HH:mm:ss的样式显示
     *
     * @return 返回当前的时间
     */
    protected Date getTime() {
        //获取当前的时间
        return new Date();

    }

}