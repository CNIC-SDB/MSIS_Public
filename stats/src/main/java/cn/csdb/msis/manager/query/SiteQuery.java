package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author jinbao
 * @Date 2019/8/15 16:04
 * @Description
 **/
@Getter
@Setter
public class SiteQuery extends BaseQuery {

	/**
	 * site_id
	 */
	private Integer id;

	/**
	 * siteIds
	 */
	private Integer[] ids;

	/**
	 * site_parentGroup_id
	 */
	private Integer gid;

	/**
	 * site_group 对应基础站点Id
	 */
	private Integer unitId;

	/**
	 * subject_id
	 */
	private Integer subject;

	/**
	 * site 日志类型
	 */
	private String format;

	/**
	 * 自定义日志格式
	 */
	private String formatPersonalized;

	/**
	 * site 上传日志目录
	 */
	private String logfiles;

	/**
	 * site Awstats 配置文件标识
	 */
	private String uri;

	/**
	 * site 中文名称
	 */
	private String cnName;

	/**
	 * site 英文名称
	 */
	private String enName;

	/**
	 * site 所属院所名称
	 */
	private String instituteName;

	/**
	 * site 日志类型
	 */
	private String type;

	/**
	 * site 统计信息备注
	 */
	private String statsNote;

	/**
	 * site 描述
	 */
	private String description;

	/**
	 * 监控点ID
	 */
	private Integer mid;

	/**
	 * 监控点名称
	 */
	private String mName;

	/**
	 * 监控URI
	 */
	private String mUri;

	/**
	 * 监控站点 描述信息
	 */
	private String mDescription;

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmUri() {
		return mUri;
	}

	public void setmUri(String mUri) {
		this.mUri = mUri;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}
}
