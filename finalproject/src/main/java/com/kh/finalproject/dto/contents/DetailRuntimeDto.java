package com.kh.finalproject.dto.contents;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//개별 상세 조회 API (/{type}/{id})에서 runtime 정보를 받기 위한 최소 DTO
@Data @NoArgsConstructor @AllArgsConstructor
public class DetailRuntimeDto {
 // 영화: runtime, TV: episode_run_time
 @JsonAlias({"runtime", "episode_run_time"})
 private List<Integer> runtimes; // TV는 리스트로 오기 때문에 List<Integer>로 처리
 
 // 영화의 경우 단일 필드인 runtime을 직접 매핑
 private Integer runtime;
 
 public Integer getSingleRuntime() {
     if (runtime != null) { // 영화인 경우
         return runtime;
     }
     // TV 시리즈인 경우, runtimes 리스트의 첫 번째 값 사용
     if (runtimes != null && !runtimes.isEmpty()) {
         return runtimes.get(0);
     }
     return null;
 }
}