package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_general")
@Getter
@Setter
public class AwGeneral implements Serializable {

	private static final long serialVersionUID = -3604034850062838474L;
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

	@Column(name = "total_visits")
	private Long totalVisits;

	@Column(name = "total_unique")
	private Long totalUnique;

}