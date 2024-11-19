package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSearchRequest {

	private String keyword;		// 검색 키워드 : 제목 검색
	private String nickname;	// 담당자 닉네임
	private String startDate;	// 일정 생성일 시작 범위
	private String endDate;		// 일정 생성일 종료 범위
	private int page = 1;
	private int size = 10;

}
