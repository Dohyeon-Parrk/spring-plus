package org.example.expert.domain.manager.entity;

public enum LogMessage {

	SAVE_SUCCESS("담당자 등록 성공"),
	SAVE_FAIL("담당자 등록 실패"),
	SAVE_FAIL_INVALID_TODO("일정이 존재하지 않습니다"),
	SAVE_FAIL_INVALID_USER("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다"),
	SAVE_FAIL_INVALID_MANAGER("등록하려고 하는 담당자 유저가 존재하지 않습니다."),
	SAVE_FAIL_SELF_ASSIGN("일정 작성자는 본인을 담당자로 등록할 수 없습니다"),
	DELETE_SUCCESS("담당자 삭제 성공"),
	DELETE_FAIL("담당자 삭제 실패"),
	DELETE_FAIL_NOT_FOUND("삭제하려는 담당자가 존재하지 않습니다"),
	DELETE_FAIL_UNAUTHORIZED("삭제 권한이 없는 사용자입니다");

	private final String message;

	LogMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
