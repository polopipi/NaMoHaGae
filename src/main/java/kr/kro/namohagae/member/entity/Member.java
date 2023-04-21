package kr.kro.namohagae.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Member {
    private Integer memberNo;
    private Integer townNo;
    private String memberEmail;
    private String memberPassword;
    private String memberNickname;
    private String memberPhone;
    private Integer memberPoint;
    private Integer memberLatitude;
    private Integer memberLongitude;
    private Integer memberGrade;
    private String memberIntroduce;
    private Boolean memberDogSignEnabled;
    private Boolean memberEnabled;
    private Integer memberLoginCount;
    private Integer memberQuestionSelectCount;
    private Boolean memberLocationEnabled;
}