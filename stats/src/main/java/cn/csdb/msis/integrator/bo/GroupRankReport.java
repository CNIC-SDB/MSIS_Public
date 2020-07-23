package cn.csdb.msis.integrator.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Author jinbao
 * @Date 2019/7/31 15:19
 * @Description
 **/
@Getter
@Setter
public class GroupRankReport implements Serializable {

	private static final long serialVersionUID = -8410850939831525625L;

	private Map<String, Object> chartData;

	private List<Field> tableData;

	@Getter
	@Setter
	public class UserRankReport {

		private BigDecimal acPages = new BigDecimal(0);
		private BigDecimal eduPages = new BigDecimal(0);
		private BigDecimal bothPages = new BigDecimal(0);
		private BigDecimal otherPages = new BigDecimal(0);

		private BigDecimal acBandwidth = new BigDecimal(0);
		private BigDecimal eduBandwidth = new BigDecimal(0);
		private BigDecimal bothBandwidth = new BigDecimal(0);
		private BigDecimal otherBandwidth = new BigDecimal(0);

		private BigDecimal acPagePercent = new BigDecimal(0);
		private BigDecimal eduPagePercent = new BigDecimal(0);
		private BigDecimal bothPagePercent = new BigDecimal(0);

		private BigDecimal acBandwidthPercent = new BigDecimal(0);
		private BigDecimal eduBandwidthPercent = new BigDecimal(0);
		private BigDecimal bothBandwidthPercent = new BigDecimal(0);


		private String groupName;

		private Integer groupId;
	}

	// 排名方式比较器
	public class URAcPagesComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getAcPages().compareTo(ur1.getAcPages());
		}
	}

	public class UREduPagesComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getEduPages().compareTo(ur1.getEduPages());
		}
	}

	public class URBothPagesComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getBothPages().compareTo(ur1.getBothPages());
		}
	}

	public class URAcPagePercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getAcPagePercent().compareTo(ur1.getAcPagePercent());
		}
	}

	public class UREduPagePercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getEduPagePercent().compareTo(ur1.getEduPagePercent());
		}
	}

	public class URBothPagePercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			return ur2.getBothPagePercent().compareTo(ur1.getBothPagePercent());
		}
	}

	public class URAcBandwidthComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getAcBandwidth().doubleValue() == 0 && ur2.getAcBandwidth().doubleValue() == 0) {
				return ur2.getAcPages().compareTo(ur1.getAcPages());
			}
			return ur2.getAcBandwidth().compareTo(ur1.getAcBandwidth());
		}
	}

	public class UREduBandwidthComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getEduBandwidth().doubleValue() == 0 && ur2.getEduBandwidth().doubleValue() == 0) {
				return ur2.getEduPages().compareTo(ur1.getEduPages());
			}
			return ur2.getEduBandwidth().compareTo(ur1.getEduBandwidth());
		}
	}

	public class URBothBandwidthComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getBothBandwidth().doubleValue() == 0 && ur2.getBothBandwidth().doubleValue() == 0) {
				return ur2.getBothPages().compareTo(ur1.getBothPages());
			}
			return ur2.getBothBandwidth().compareTo(ur1.getBothBandwidth());
		}
	}

	public class URAcBandwidthPercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getAcBandwidth().doubleValue() == 0 && ur2.getAcBandwidth().doubleValue() == 0) {
				return ur2.getAcPages().compareTo(ur1.getAcPages());
			}
			return ur2.getAcBandwidthPercent().compareTo(ur1.getAcBandwidthPercent());
		}
	}

	public class UREduBandwidthPercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getEduBandwidth().doubleValue() == 0 && ur2.getEduBandwidth().doubleValue() == 0) {
				return ur2.getEduPages().compareTo(ur1.getEduPages());
			}
			return ur2.getEduBandwidthPercent().compareTo(ur1.getEduBandwidthPercent());
		}
	}

	public class URBothBandwidthPercentComparator implements Comparator<UserRankReport> {

		public int compare(UserRankReport ur1, UserRankReport ur2) {
			if (ur1.getBothBandwidth().doubleValue() == 0 && ur2.getBothBandwidth().doubleValue() == 0) {
				return ur2.getBothPages().compareTo(ur1.getBothPages());
			}
			return ur2.getBothBandwidthPercent().compareTo(ur1.getBothBandwidthPercent());
		}
	}


}

