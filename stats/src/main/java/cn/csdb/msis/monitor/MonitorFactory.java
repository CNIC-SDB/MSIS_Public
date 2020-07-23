package cn.csdb.msis.monitor;

/**
 * 监控工厂类
 *
 * @author CXY
 */
public class MonitorFactory {
    private Monitor monitor;

    /**
     *
     */
    public MonitorFactory() {
        super();
        // 创建各个ExceptionProcessor
        monitor = new HTTPMonitor();
    }

    /**
     * 获取一个Monitor，如果不存在则创建之，否则直接返回
     *
     * @param type
     * @return Monitor
     */
    public Monitor getMonitor() {
        Monitor monitor = null;
		/*
		type = type.trim();
		if (type.equalsIgnoreCase("http")) {
			monitor = monitors[0];
		} else if (type.equalsIgnoreCase("sql")) {
			monitor = monitors[1];
		} else if (type.equalsIgnoreCase("other")) {
			monitor = monitors[1];
		}
		*/
        monitor = this.monitor;
        return monitor;
    }
}