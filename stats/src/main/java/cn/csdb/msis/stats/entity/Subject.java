package cn.csdb.msis.stats.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "subjects")
@Getter
@Setter
public class Subject implements Serializable {

	private static final long serialVersionUID = -7744485026990916813L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;

	private String description;

/*	@OneToMany
    @JoinTable(name = "subject_group"

    )
	private Set<Group> groups;*/

	@JsonBackReference
	@OneToMany
	@JoinTable(name = "subject_site",
			joinColumns = {@JoinColumn(name = "subject_id")},
			inverseJoinColumns = {@JoinColumn(name = "site_id")})
	private Set<Site> sites;

}