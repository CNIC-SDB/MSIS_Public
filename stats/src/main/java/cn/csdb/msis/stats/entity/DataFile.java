package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "datafile")
@Getter
@Setter
public class DataFile implements Serializable {
	private static final long serialVersionUID = 6396084619856776871L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private Long lastModifyDate;

	private Long size;

}