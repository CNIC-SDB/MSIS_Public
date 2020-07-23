package cn.csdb.msis.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements Serializable {
	private static final long serialVersionUID = -1260206260935420709L;

	public static final String ADMIN = "ROLE_ADMIN";

	public static final String USER = "ROLE_USER";
	@Id
	@Column(name = "rolename")
	private String rolename;

	private String roledesc;

	@JsonBackReference
	@ManyToMany(mappedBy = "roles")
	private Set<User> users;

}