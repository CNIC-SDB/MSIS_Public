package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author jinbao
 * @Date 2019/8/14 15:11
 * @Description 分页参数
 **/
@Getter
@Setter
public class BaseQuery {

	private Integer pageSize = 10;

	private Integer pageNumber = 1;

	private String sortName;

	private String sortOrder = "asc";

}
