package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subject_group")
@Getter
@Setter
public class SubjectGroup implements Serializable {
    private static final long serialVersionUID = -6130276042215711495L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "subject_id")
    private Integer subjectId;

    @Column(name = "group_id")
    private Integer groupId;
}
