package cn.csdb.msis.monitor;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Date;

/**
 * 主要完成对Http连接服务的处理
 *
 * @author CXY
 */
public class HTTPMonitor extends AbstractMonitor {

    private final static String module = HTTPMonitor.class.getName();


    /**
     * 测试状态是否异常
     *
     * @param statusCode 要测试的状态
     * @return boolean 返回该状态是否异常,true是异常
     */
    public boolean isExceptionCode(String statusCode) {
        try {
            int code = Integer.parseInt(statusCode);
            //4**，5**,600,700,800是异常.其中800是httpexception,
            //700是超时或者重定向错误，600是其他错误
            if ((code >= 400) && (code <= 900))
                return true;
            else//1**、2**和3**是正常，
                return false;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 获得监控点当前的状态
     *
     * @param mp 监控点的信息
     * @return ServiceStatus 返回监控点的服务状态
     */
    public ServiceStatus getStatus(cn.csdb.msis.stats.entity.Monitor mp) {

        int statuscode = 900;
        HttpMethod method = null;

        try {
            //java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
            int httpTimeout = 9000;
            int socketTimeout = 18000;

            InitialContext ic = new InitialContext();
            // 创建HttpClient，并设置上下文
            HttpClient client = new HttpClient();
            //设置Http连接超时时间,单位是毫秒
            client.getHttpConnectionManager().getParams()
                    .setConnectionTimeout(httpTimeout);
            //设置socket超时时间,单位是毫秒
            client.getHttpConnectionManager().getParams().setSoTimeout(socketTimeout);
            //subtype = subtype.trim();
            client.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);

            String url;
            if (mp.getUri().startsWith("http")) {
                url = mp.getUri();
            } else {
                url = "http://" + mp.getUri();
            }
            method = new GetMethod(url);
            client.executeMethod(method);

            if (method != null) {
                //获得连接的状态
                statuscode = method.getStatusCode();

                //重定向处理
                while ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)
                        || (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
                        || (statuscode == HttpStatus.SC_SEE_OTHER)
                        || (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    Header header = method.getResponseHeader("location");
                    if (header != null) {
                        String newuri = header.getValue();
                        if ((newuri == null) || (newuri.equals(""))) {
                            newuri = "/";
                        }
                        GetMethod redirect = new GetMethod(newuri);
                        statuscode = client.executeMethod(redirect);
                        redirect.releaseConnection();
                    } else {
                        statuscode = 700;
                    }
                }
                if ((statuscode < 400) && mp.getKeyword() != null && !mp.getKeyword().equals("")) {
                    String responseString = new String(method.getResponseBodyAsString().getBytes("GBK"));
//                    Log.logInfo(responseString, module);
                    if (responseString.indexOf(mp.getKeyword()) != -1) {
                        statuscode = 200;
                    } else {
                        statuscode = 888;
                    }
                }

            }

        } catch (HttpException e) {
            statuscode = 800;
        } catch (IOException e) {
            statuscode = 700;
        } catch (Exception e) {
            statuscode = 600;
        } finally {
            //释放连接
            if (method != null) {
                method.releaseConnection();
            }
        }
        //返回HTTP连接状态
        return new ServiceStatus(mp.getId(), this.getTime(), Integer
                .toString(statuscode), isExceptionCode(Integer
                .toString(statuscode)));
    }

    /**
     * 获取当前的时间，并以yyyy-MM-dd HH:mm:ss的样式显示
     *
     * @return 返回当前的时间
     */
    protected Date getTime() {
        //获取当前的时间
		/*
		Calendar currentTime = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(currentTime.getTime());
		*/
        return new Date();

    }

}