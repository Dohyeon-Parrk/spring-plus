package org.example.expert.domain.manager.service;

import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.entity.LogAction;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogRepository	logRepository;

	// propagation = Propagation.REQUIRES_NEW : 현재 진행중인 Transaction 이 있더라도 일시 정지하고 새로운 트랜잭션을 생성한다. -> 기존 Transaction 과 독립적으로 작동함
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(LogAction action, String message){
		Log log = new Log(action, message);
		logRepository.save(log);
	}
}
