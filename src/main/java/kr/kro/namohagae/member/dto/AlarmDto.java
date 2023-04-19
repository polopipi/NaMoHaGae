package kr.kro.namohagae.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

public class AlarmDto {
    @Data
    public static class FindAll {
        private Integer alarmNo;
        private String alarmContent;
        private String alarmRink;
        private Boolean alarmReadEnabled;
    }
    @Data
    @ToString
    @AllArgsConstructor
    public static class Pagination {
        private Integer pageno;
        private Integer prev;
        private Integer start;
        private Integer end;
        private Integer next;
        private List<FindAll> board;
    }

}
