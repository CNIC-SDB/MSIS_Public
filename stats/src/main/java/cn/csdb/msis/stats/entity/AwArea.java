package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_area")
@Getter
@Setter
public class AwArea implements Serializable {

	private static final long serialVersionUID = -7050109619320868177L;
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

	private String area;

	private Long ips;
}