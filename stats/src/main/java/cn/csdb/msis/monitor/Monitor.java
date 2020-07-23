package cn.csdb.msis.monitor;



/**
 * 监控接口
 *
 * @author CXY
 */
public interface Monitor {
    /**
     * 检查监控点服务的状态是否正常
     *
     * @param statusCode 服务的状态
     * @return boolean 服务的状态是否正常,true是正常，false是异常
     */
    public boolean isExceptionCode(String statusCode);

    /**
     * 得到监控点的当前服务状态
     *
     * @param mp 监控点的信息
     * @return 返回该监控点的服务状态
     */
    public ServiceStatus getStatus(cn.csdb.msis.stats.entity.Monitor mp);


}
