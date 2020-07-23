package cn.csdb.msis.stats.entity;

import cn.csdb.msis.user.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "groups")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Group implements Serializable {
	private static final long serialVersionUID = -3176294048599014854L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 根节点级别
	 */
	public static Integer POINT_ROOT = 0;

	/**
	 * 类目节点级别
	 */
	public static Integer POINT_CLASS = 10;


	/**
	 * 院所节点级别
	 */
	public static Integer POINT_INSTITUTE = 20;


	/**
	 * 院所中包含节点级别
	 */
	public static Integer POINT_PART_OF_INSTITUTE = 30;

	/**
	 * 排名节点级别
	 */
	public static Integer POINT_RANK = 40;

	/**
	 * 基础节点级别
	 */
	public static Integer POINT_UNIT = 50;

	/**
	 * 节点级别
	 */
	private Integer gtype;

	private String enName;

	private String cnName;

	private String description;

	private Integer pid;

	private String shortName;

	private Integer isRank;

	// 排名站点群所属院所
	private Integer iid;

	// 排名站点群是否显示
	private Integer isShow;

	// 排名站点群排名标识
	private Double orderNum;
	/**
	 * 院所节点|排名节点 下属实体站点
	 */
	@JsonBackReference
	@ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "parentGroup", fetch = FetchType.LAZY)
	private Set<Site> sites;

	/**
	 * 实体站点对应基础节点
	 */
	@OneToOne(cascade = CascadeType.REFRESH, mappedBy = "group")
	@JsonBackReference
	private Site site;

	@JsonBackReference
	@OneToOne(mappedBy = "group")
	private User user;

	@JsonBackReference
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "pid", referencedColumnName = "id")
	private Set<Group> childGroups;

}