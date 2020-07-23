package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_filetype")
@Getter
@Setter
public class AwFileType implements Serializable {

	private static final long serialVersionUID = 3817418256680200428L;
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

	private Long pages;

	private Long hits;

	private Long bandwidth;

	@Column(name = "file_type")
	private String fileType;


}