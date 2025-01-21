package team11.team11project.mock;

import java.time.LocalTime;

import team11.team11project.common.entity.Member;
import team11.team11project.common.entity.Store;
import team11.team11project.common.enums.UserRole;

/**
 * 공유 객체 만들기
 */

public class MockData {

	public static Member createMember(){
		return new Member(
			1L,
			"망곰이",
			"test@gmail.com",
			"1234",
			UserRole.fromString("OWNER"),
			false
		);
	}

	public static Store createStore(){
		return new Store(
			1L,
			"망곰이",
			createMember(),
			1000,
			LocalTime.of(7, 30, 0),
			LocalTime.of(23, 30, 0),
			false
		);
	}
}
