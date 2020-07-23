package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author jinbao
 * @Date 2019/8/14 16:10
 * @Description
 **/
@Getter
@Setter
public class UserQuery extends BaseQuery {
	private Integer id;
	private String cnName;
	private String enName;
	private String username;
	private String oldUsername;
	private String password;
	private String email;
	private String mobile;
	private Integer gid;
	private Integer monitorMail;
	private String description;
}
