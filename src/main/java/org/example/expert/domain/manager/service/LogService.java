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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(LogAction action, String message){
		Log log = new Log(action, message);
		logRepository.save(log);
	}
}
