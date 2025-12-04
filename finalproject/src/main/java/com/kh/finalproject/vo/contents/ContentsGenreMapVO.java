package com.kh.finalproject.vo.contents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ContentsGenreMapVO {
	private Long contentsId;
	private Integer genreId;
}
