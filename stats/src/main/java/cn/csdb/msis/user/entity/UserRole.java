package cn.csdb.msis.user.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author jinbao
 * @Date 2019/9/27 10:40
 * @Description
 **/
@Entity
@Table(name = "user_role")
@Getter
@Setter
public class UserRole implements Serializable {
	private static final long serialVersionUID = -2228254781376048330L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username")
	private String username;

	@Column(name = "rolename")
	private String rolename;
}
