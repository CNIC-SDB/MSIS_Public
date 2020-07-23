package cn.csdb.msis.manager.service;

import cn.csdb.msis.integrator.util.DateUtil;
import cn.csdb.msis.stats.repository.StatusTempRepository;
import cn.csdb.msis.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.Tuple;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Author jinbao
 * @Date 2019/10/30 15:22
 * @Description 邮件统计
 **/
@Service
public class SendMailService {

	private Logger logger = LoggerFactory.getLogger(SendMailService.class);

	@Autowired
	private StatusTempRepository statusTempRepository;

	@Value(value = "${mail.smtp.auth}")
	private String auth;

	@Value(value = "${mail.smtp.host}")
	private String host;

	@Value(value = "${mail.transport.protocol}")
	private String protocol;

	@Value(value = "${mail.senderAddress}")
	private String senderAddress;

	@Value(value = "${mail.senderAccount}")
	private String senderAccount;

	@Value(value = "${mail.senderPassword}")
	private String senderPassword;

	@Value(value = "${custom.monitor.hours}")
	private int hours;

	@Value(value = "${custom.url}")
	private String url;

	@Value(value = "${custom.userTitle}")
	private String userTitle;

	@Value(value = "${mail.userMessageSubject}")
	private String userMessageSubject;

	@Value(value = "${mail.monitorMessageSubject}")
	private String monitorMessageSubject;

	@Value(value = "${custom.monitor.hours}")
	private Integer monitorHours;

	/**
	 * 用户注册完成后的邮件通知
	 *
	 * @param user
	 */
	public void addUserMessage(User user) {
		Session session = initSession();
		MimeMessage message = null;
		try {
			message = getMimeMessage(session, user.getEmail());
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("userTitle", userTitle);
			context.setVariable("ftpPassword", user.getFtpPasswordTemp());
			context.setVariable("url", url);
			message.setSubject(userMessageSubject);
			message.setContent(messageBody("userAddMailTemplate", context), "text/html;charset=UTF-8");
			transportMsg(session, message);
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.error(user.getEnName() + "用户信息初始化邮件发送失败!");
		}
	}

	/**
	 * 用户信息变更邮件通知
	 *
	 * @param user
	 */
	public void editUserMessage(User user) {
		Session session = initSession();
		MimeMessage message = null;
		try {
			message = getMimeMessage(session, user.getEmail());
			Context context = new Context();
			context.setVariable("user", user);
			context.setVariable("userTitle", userTitle);
			context.setVariable("ftpPassword", user.getFtpPasswordTemp());
			context.setVariable("url", url);
			message.setSubject(userMessageSubject);
			message.setContent(messageBody("userEditMailTemplate", context), "text/html;charset=UTF-8");
			transportMsg(session, message);
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.error(user.getEnName() + "用户信息变更邮件发送失败!");
		}
	}

	/**
	 * 站点连续异常邮件通知
	 */
	public void monitorMessage() {
		List<Tuple> accordStatus = countStatus();
		Session session = initSession();
		for (Tuple tuple : accordStatus) {
			if (tuple.get("result").toString().split(",").length - 1 == monitorHours && Integer.parseInt(tuple.get("userIsMail").toString()) == 1) {
				try {
					logger.info(tuple.get("userCnName").toString() + " 管理员的 " + tuple.get("siteCnName").toString() + " 监控到异常需要邮件通知！！！");
					MimeMessage message = getMimeMessage(session, tuple.get("userEmail").toString());
					Context context = new Context();
					context.setVariable("userCnName", tuple.get("userCnName").toString());
					context.setVariable("siteCnName", tuple.get("siteCnName").toString());
					context.setVariable("monitorHours", monitorHours);
					context.setVariable("url", tuple.get("uri").toString());
					context.setVariable("userTitle", userTitle);
					message.setSubject(monitorMessageSubject);
					message.setContent(messageBody("monitorMailTemplate", context), "text/html;charset=UTF-8");
					transportMsg(session, message);
					logger.info(tuple.get("userCnName").toString() + " 管理员的 " + tuple.get("siteCnName").toString() + " 监控异常邮件发送成功！！！");
				} catch (MessagingException e) {
					e.printStackTrace();
					logger.error(tuple.get("userCnName").toString() + " 管理员的 " + tuple.get("siteCnName").toString() + " 监控异常邮件发送失败！！！");
				}

			}
		}
	}

	/**
	 * 删除超时监控信息，并统计此时段内的监控情况
	 *
	 * @return
	 */
	private List<Tuple> countStatus() {
		String aFewHoursAgo = DateUtil.aFewHoursAgo(monitorHours);
		statusTempRepository.deleteOvertimeStatus(aFewHoursAgo);
		logger.info("删除状态缓存库 \"" + aFewHoursAgo + "\" 时间之前的数据");
		return statusTempRepository.findAccordStatus();
	}

	/**
	 * 创建定义整个应用程序所需的环境信息的 Session 对象
	 *
	 * @return
	 */
	private Session initSession() {
		// 连接邮件服务器的参数配置
		Properties properties = new Properties();
		// 设置用户的认证方式
		properties.setProperty("mail.smtp.auth", auth);
		// 设置传输协议
		properties.setProperty("mail.transport.protocol", protocol);
		// 设置发件人的SMTP服务器地址
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getInstance(properties);
		//设置调试信息在控制台打印出来
		session.setDebug(true);
		return session;
	}

	/**
	 * 获得创建一封邮件的实例对象
	 *
	 * @param recipientAddress 接收信息邮箱账号
	 * @return
	 * @throws MessagingException
	 * @throws AddressException
	 */
	private MimeMessage getMimeMessage(Session session, String recipientAddress) throws MessagingException {
		//3、创建一封邮件的实例对象
		MimeMessage msg = new MimeMessage(session);
		//设置发件人地址
		msg.setFrom(new InternetAddress(senderAddress));
		/**
		 * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
		 * MimeMessage.RecipientType.TO:发送
		 * MimeMessage.RecipientType.CC：抄送
		 * MimeMessage.RecipientType.BCC：密送
		 */
		msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientAddress));
		//设置邮件的发送时间,默认立即发送
		msg.setSentDate(new Date());
		//结束
		return msg;
	}

	/**
	 * 实际发送行为
	 *
	 * @param session
	 * @param msg
	 * @throws MessagingException
	 */
	private void transportMsg(Session session, MimeMessage msg) throws MessagingException {
		// 根据session对象获取邮件传输对象Transport
		Transport transport = session.getTransport();
		// 设置发件人的账户名和密码
		transport.connect(senderAccount, senderPassword);
		// 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
		transport.sendMessage(msg, msg.getAllRecipients());

		//如果只想发送给指定的人，可以如下写法
		//transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});

		//5、关闭邮件连接
		transport.close();
	}

	/**
	 * 获得实际发送内容
	 *
	 * @param templateName 模板名称
	 * @param context      模板参数处理器
	 * @return
	 */
	private String messageBody(String templateName, Context context) {
		String messageBody = "";

		// 模板引擎配置
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("/templates/awstatsConf/");
		resolver.setSuffix(".txt");
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(resolver);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));
		try {
			// 处理模板生成awstats配置文件到指定目录
			templateEngine.process(templateName, context, bufferedWriter);
			messageBody = byteArrayOutputStream.toString();
			logger.info("模板结果：\n" + messageBody);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messageBody;
	}
}
