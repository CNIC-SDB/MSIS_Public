package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_day")
@Getter
@Setter
public class AwDay implements Serializable {

	private static final long serialVersionUID = 4623311444479568561L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uid")
	private Site site;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fid")
	private DataFile dataFile;

	private Integer year;

	private Integer month;

	private Integer day;

	private Long pages;

	private Long hits;

	private Long bandwidth;

	//	@Column(name = "total_visits")
	@Column(name = "visits")
	private Long visits;
}