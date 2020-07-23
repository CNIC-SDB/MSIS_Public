package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity()
@Table(name = "aw_achost")
@Getter
@Setter
public class AwAchost implements Serializable {

    private static final long serialVersionUID = -4741804341419182321L;
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

    @Column(name = "host_type")
    private Integer hostType;

    private Long pages;

    private Long hits;

    private Long bandwidth;

    private Long ips;
}