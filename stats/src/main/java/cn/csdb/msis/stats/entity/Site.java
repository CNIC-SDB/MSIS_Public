package cn.csdb.msis.stats.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "sites")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Site implements Serializable {
	private static final long serialVersionUID = 1962382531172206647L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String cnName;

	private String enName;

	/**
	 * 站点类型
	 */
	private String type;

	/**
	 * 站点所属院所Id
	 */
	private Integer gid;

	/**
	 * 站点当前状态  0:未启用
	 */
	private Integer status;

	/**
	 * 站点统计信息备注
	 */
	private String statsNote;

	/**
	 * 站点原始日志文件位置
	 */
	private String logfiles;

	/**
	 * 站点统计日志类型
	 */
	private String format;

	/**
	 * 站点统计日志pattern
	 */
	private String pattern;

	/**
	 * 站点域名
	 */
	private String uri;

	private String description;

	// @JoinTable 指定中间表
	//   joinColumns 指定中间表(group_site)与当前表(sites)的字段映射关系
	//   inverseJoinColumns 指定中间表(group_site)与另外一张关系表(groups)的字段映射关系
	/**
	 * 实体站点所属院所节点
	 */
	@ManyToMany
	@JsonBackReference
	@JoinTable(name = "group_site",
			joinColumns = {@JoinColumn(name = "sid"/*, referencedColumnName = "id"*/)},
			inverseJoinColumns = {@JoinColumn(name = "gid"/*, referencedColumnName = "id"*/)}
	)
	private Set<Group> parentGroup = new HashSet<Group>();

	/**
	 * 实体站点对应基础节点
	 */
	@JsonBackReference
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinColumn(name = "unit_id", referencedColumnName = "id")
	private Group group;

	@JsonBackReference
	@ManyToOne
	@JoinTable(name = "subject_site",
			joinColumns = {@JoinColumn(name = "site_id")},
			inverseJoinColumns = {@JoinColumn(name = "subject_id")})
	private Subject subject;


	@OneToMany(mappedBy = "site", fetch = FetchType.EAGER)
	@JsonBackReference
	private Set<Monitor> monitors;

	@JsonBackReference
	@OneToOne(mappedBy = "site")
	private Monitor monitor;


	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwAchost> awAchosts;

	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwArea> awAreas;

	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwDomain> awDomains;

	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwFileType> awFileTypes;

	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwGeneral> awGenerals;

	@OneToMany
	@JsonBackReference
	@JoinColumn(name = "uid")
	private List<AwTime> awTimes;

}