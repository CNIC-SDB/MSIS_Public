package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_time")
@Getter
@Setter
public class AwTime implements Serializable {

	private static final long serialVersionUID = 7046135842855899363L;
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

	private Integer hour;

	private Long pages;

	private Long hits;

	private Long bandwidth;

	@Column(name = "non_viewed_pages")
	private Long nonViewedPages;

	@Column(name = "non_viewed_hits")
	private Long nonViewedHits;

	@Column(name = "non_bandwidth")
	private Long nonViewedBandwidth;


}