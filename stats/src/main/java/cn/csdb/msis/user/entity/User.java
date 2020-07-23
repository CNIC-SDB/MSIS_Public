package cn.csdb.msis.user.entity;

import cn.csdb.msis.stats.entity.Group;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class User implements Serializable {

	private static final long serialVersionUID = 7765482183104492050L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username;

	private String password;

	private String email;

	private String mobile;

	@Column(name = "cn_name")
	private String cnName;

	@Column(name = "en_name")
	private String enName;

	private String description;

	/**
	 * 是否主权限
	 */
	private Integer isroot;

	/**
	 * 是否邮件通知监控异常
	 * 默认通知
	 */
	@Column(name = "monitor_mail")
	private Integer monitorMail = 1;

	/**
	 * 初始化的ftp密码
	 */
	@Transient
	private String ftpPasswordTemp;

	/**
	 * 原始未加密密码
	 */
	@Transient
	private String transientPassword;

	@JsonBackReference
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
			joinColumns = {@JoinColumn(name = "username", referencedColumnName = "username")},
			inverseJoinColumns = {@JoinColumn(name = "rolename", referencedColumnName = "rolename")}
	)
	private Set<Role> roles;

	@JsonBackReference
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "gid", referencedColumnName = "id")
	private Group group;


}