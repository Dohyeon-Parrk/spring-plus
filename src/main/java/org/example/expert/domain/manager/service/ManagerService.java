package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.entity.LogAction;
import org.example.expert.domain.manager.entity.LogMessage;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final LogService logService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(LogAction action, LogMessage message){
        logService.saveLog(action, message.getMessage());
    }

    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        // 일정을 만든 유저
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_INVALID_TODO);
                    return new InvalidRequestException(LogMessage.SAVE_FAIL_INVALID_TODO.getMessage());
                });

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_INVALID_USER);
            throw new InvalidRequestException(LogMessage.SAVE_FAIL_INVALID_USER.getMessage());
        }

        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                .orElseThrow(() -> {
                    logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_INVALID_MANAGER);
                    return new InvalidRequestException(LogMessage.SAVE_FAIL_INVALID_MANAGER.getMessage());
                });

        if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
            logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_SELF_ASSIGN);
            throw new InvalidRequestException(LogMessage.SAVE_FAIL_SELF_ASSIGN.getMessage());
        }

        try {
            Manager newManagerUser = new Manager(managerUser, todo);
            Manager savedManagerUser = managerRepository.save(newManagerUser);

            logService.saveLog(LogAction.SAVE_SUCCESS, LogMessage.SAVE_SUCCESS.getMessage());

            return new ManagerSaveResponse(
                    savedManagerUser.getId(),
                    new UserResponse(managerUser.getId(), managerUser.getEmail())
            );
        } catch (Exception e){
            logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL);
            throw  e;
        }

    }

    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> {
                logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_INVALID_TODO);
                return new InvalidRequestException(LogMessage.SAVE_FAIL_INVALID_TODO.getMessage());
            });

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        User user = User.fromAuthUser(authUser);

        Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> {
                logFailure(LogAction.SAVE_FAIL, LogMessage.SAVE_FAIL_INVALID_TODO);
                return new InvalidRequestException(LogMessage.SAVE_FAIL_INVALID_TODO.getMessage());
            });

        if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
            logFailure(LogAction.DELETE_FAIL, LogMessage.DELETE_FAIL_NOT_FOUND);
            throw new InvalidRequestException(LogMessage.DELETE_FAIL_NOT_FOUND.getMessage());
        }

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> {
                    logFailure(LogAction.DELETE_FAIL, LogMessage.DELETE_FAIL_UNAUTHORIZED);
                    return new InvalidRequestException(LogMessage.DELETE_FAIL_UNAUTHORIZED.getMessage());
                });

        if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
            logFailure(LogAction.DELETE_FAIL, LogMessage.DELETE_FAIL_UNAUTHORIZED);
            throw new InvalidRequestException(LogMessage.DELETE_FAIL_UNAUTHORIZED.getMessage());
        }

        try {
            managerRepository.delete(manager);
            logService.saveLog(LogAction.DELETE_SUCCESS, LogMessage.DELETE_SUCCESS.getMessage());
        } catch (Exception e){
            logFailure(LogAction.DELETE_FAIL, LogMessage.DELETE_FAIL);
            throw e;
        }
    }
}
