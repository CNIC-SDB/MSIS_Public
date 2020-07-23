package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/9/2 10:21
 * @Description
 **/
@Getter
@Setter
public class GroupQuery extends BaseQuery {

	private Integer id;

	private Integer pid;

	private String cnName;

	private String pcnName;

	private String shortName;

	private String description;

	private Integer gtype;

	// 排名站点群所属院所
	private Integer iid;

	// 排名站点群是否显示
	private Integer isShow;

	// 排名站点群排名标识
	private Double orderNum;

	private Boolean isParent = false;

	private Boolean open = false;

	private List<GroupQuery> children;

	public class GroupQueryCompare implements Comparator<GroupQuery> {

		@Override
		public int compare(GroupQuery o1, GroupQuery o2) {
			int result = 0;
			if (o1.getOrderNum() != null && o2.getOrderNum() != null) {
				result = o1.getOrderNum().compareTo(o2.getOrderNum());
			}
			return result;
		}
	}
}
