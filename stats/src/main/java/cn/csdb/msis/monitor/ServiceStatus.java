package cn.csdb.msis.monitor;

import java.util.Date;

/**
 * 服务状态类
 *
 * @author CXY
 */
public class ServiceStatus {

    //监控点的ID
    private int mid;

    //监控发生的时间
    private Date time;

    //监控点的状态
    private String status;

    //监控点状态是否异常，目的是方便写到异常表exception_log中
    private boolean exceptionStatus;

    public ServiceStatus() {

    }

    public ServiceStatus(int mid, Date time, String status) {
        this.mid = mid;
        this.time = time;
        this.status = status;
    }

    public ServiceStatus(int mid, Date time, String status, boolean exception) {
        this.mid = mid;
        this.time = time;
        this.status = status;
        this.exceptionStatus = exception;
    }

    public int getMID() {
        return mid;
    }

    public void setMID(int mid) {
        this.mid = mid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(boolean exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }
}