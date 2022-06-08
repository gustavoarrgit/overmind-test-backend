package com.overmindtest.overmindtest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerDTO {
	private Integer position;
	private String title;
	private Double rating;
	private List<String> casting;
	private String director;
	private String comment;

	@Override
	public String toString() {
		super.toString();

		return this.title + " Rating - " + this.rating + " Director - " + this.director + " Casting - "
				+ casting.toString() + " Comment - " + comment;
	}
}
