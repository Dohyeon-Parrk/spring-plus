package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {

	private final String title;
	private final long managerCount;	// 등록 담당자 수
	private final long commentCount;	// 등록된 댓글 수

	public TodoSearchResponse(String title, long managerCount, long commentCount) {
		this.title = title;
		this.managerCount = managerCount;
		this.commentCount = commentCount;
	}
}
