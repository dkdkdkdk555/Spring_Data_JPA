package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username + ' '+ target.age}") // 엔티티가져온다음 원하는데이터 찍어다가 이거대로 계산 후 리턴 => "m1 0" 이 출력됨
    String getUsername();

}
