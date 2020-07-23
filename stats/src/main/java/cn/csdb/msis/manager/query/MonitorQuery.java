package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author jinbao
 * @Date 2019/8/28 14:22
 * @Description
 **/
@Getter
@Setter
public class MonitorQuery extends BaseQuery {

	private Integer id;

	private Integer sid;

	private String name;

	private String type;

	private String uri;

	private String keyword;

	private String description;

	private Integer flag;
}
