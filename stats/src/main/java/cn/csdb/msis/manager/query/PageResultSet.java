package cn.csdb.msis.manager.query;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/8/14 15:28
 * @Description Page<T> 转换
 **/
@Getter
@Setter
public class PageResultSet<T> {

	private Integer pageNumber;

	private Long total;

	private List<T> rows;
}
