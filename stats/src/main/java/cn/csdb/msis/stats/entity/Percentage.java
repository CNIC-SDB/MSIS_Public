package cn.csdb.msis.stats.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "percentages")
@Getter
@Setter
public class Percentage implements Serializable {
	private static final long serialVersionUID = 388726960297026006L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer mid;

	private Integer year;

	private Integer month;

	private Integer day;

	private Integer validhours;

	private Integer totalhours;

	private Double percent;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMid() {
		return mid;
	}

	public void setMid(Integer mid) {
		this.mid = mid;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getValidhours() {
		return validhours;
	}

	public void setValidhours(Integer validhours) {
		this.validhours = validhours;
	}

	public Integer getTotalhours() {
		return totalhours;
	}

	public void setTotalhours(Integer totalhours) {
		this.totalhours = totalhours;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}
}