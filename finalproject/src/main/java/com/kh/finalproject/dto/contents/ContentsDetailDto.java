package com.kh.finalproject.dto.contents;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//MyBatis resultMap의 Target DTO가 될 ContentsDetailDTO (기존 ContentsDTO 역할 대체)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContentsDetailDto {
	private Long contentsId; 
	private String contentsTitle; 
	private String contentsType; 
	private String contentsOverview; 
	private String contentsPosterPath;
	private String contentsBackdropPath;
	private Double contentsVoteAverage;
	private Integer contentsRuntime; 
	private String contentsReleaseDate; 
 
	private List<String> genreNames; 
}
