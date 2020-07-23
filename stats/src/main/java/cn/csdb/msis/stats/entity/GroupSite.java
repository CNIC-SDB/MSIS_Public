package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "group_site")
@Getter
@Setter
public class GroupSite implements Serializable {
    private static final long serialVersionUID = 1861944575959634154L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer gid;

    private Integer sid;


}
