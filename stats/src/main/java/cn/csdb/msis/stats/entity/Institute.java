package cn.csdb.msis.stats.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "institutes")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Institute implements Serializable {
	private static final long serialVersionUID = 5081877639267311300L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String cnName;

	private String description;

	private Integer gid;

	private String enName;

	/*@JsonBackReference
	@OneToMany(mappedBy = "institutes", fetch = FetchType.LAZY)
	private Set<User> users;*/

	@JsonBackReference
	@OneToMany()
	@JoinColumn(name = "iid")
	private Set<Site> sites;

	@JsonBackReference
	@OneToMany
	@JoinColumn(name = "id")
	private Set<Group> groups;

}