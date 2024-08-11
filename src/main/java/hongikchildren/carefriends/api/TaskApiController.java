package hongikchildren.carefriends.api;

import hongikchildren.carefriends.domain.*;
import hongikchildren.carefriends.service.FriendService;
import hongikchildren.carefriends.service.TaskService;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskApiController {

    private final TaskService taskService;
    private final FriendService friendService;

    /**
     * 일정 추가
     */
    @PostMapping
    public taskResponse addTask(@RequestBody taskRequest request) {

        Friend friend = friendService.getFriendById(request.getFriendId()).orElseThrow();
        Task task = taskService.saveTask(friend, request.getDate(), request.getStartTime(), request.getTitle(),
                request.getLocation(), request.getMemo(), request.getPeriodType(), request.getPeriod());
        return new taskResponse(task.getGroupId());

    }

    @GetMapping
    public List<perTaskResponse> getTask(@RequestParam LocalDate date) {
        List<Task> task = taskService.getTask(date);
        System.out.println(task);
        List<perTaskResponse> list = task.stream().map(
                v -> perTaskResponse.builder()
                        .location(v.getLocation())
                        .memo(v.getMemo())
                        .signalTime(v.getSignalTime())
                        .startTime(v.getStartTime())
                        .status(v.getStatus())
                        .taskType(v.getTaskType())
                        .title(v.getTitle())
                        .date(date)
                        .build()
        ).toList();

        return list;
    }

//    public Task saveTask(Friend friend, LocalDate date, LocalTime startTime, String title, String location, String memo,
//                         PeriodType periodType, int period) {
    @Data
    static class taskRequest {
        private UUID friendId;
        private LocalDate date;
        private PeriodType periodType;
        private int period;
        private LocalTime startTime;
        //private LocalTime signalTime; 일단 기본 10분 전 알람
        private String title;
        private String location;
        private String memo;

        public taskRequest(UUID friendId, LocalDate date, PeriodType periodType, int period, LocalTime startTime, String title, String location, String memo) {
            this.friendId = friendId;
            this.date = date;
            this.periodType = periodType;
            this.period = period;
            this.startTime = startTime;
            this.title = title;
            this.location = location;
            this.memo = memo;
        }
    }

    @Data
    static class taskResponse {
        private long id; //이 id는 그룹 id이다.

        public taskResponse(long id) {
            this.id = id;
        }
    }

    @Data
    @Builder
    static class perTaskResponse {
        private String memo;
        private LocalTime startTime;
        private LocalTime signalTime;
        private String location;
        private String title;
        private Status status;
        private TaskType taskType;
        private LocalDate date;

        @Builder
        public perTaskResponse(String memo, LocalTime startTime, LocalTime signalTime, String location, String title, Status status, TaskType taskType, LocalDate date) {
            this.memo = memo;
            this.startTime = startTime;
            this.signalTime = signalTime;
            this.location = location;
            this.title = title;
            this.status = status;
            this.taskType = taskType;
            this.date = date;
        }
    }
}
