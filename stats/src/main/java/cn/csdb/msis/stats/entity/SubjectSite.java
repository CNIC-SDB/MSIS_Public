package cn.csdb.msis.stats.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subject_site")
@Setter
@Getter
public class SubjectSite implements Serializable {

    private static final long serialVersionUID = -1859906298315726085L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "site_id")
    private Integer siteId;
}
