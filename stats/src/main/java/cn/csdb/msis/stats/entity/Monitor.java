package cn.csdb.msis.stats.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "monitors")
@Getter
@Setter
public class Monitor implements Serializable {
	private static final long serialVersionUID = 2190219131702472015L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String type;

	private String name;

	private String uri;

	private String keyword;

	private String description;

	private Integer sid;

	@JsonBackReference
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sid", insertable = false, updatable = false)
	private Site site;

	private Integer flag;

}