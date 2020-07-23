package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ftpuser")
@Getter
@Setter
public class FtpUser implements Serializable {
	private static final long serialVersionUID = -3970786426842062200L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
