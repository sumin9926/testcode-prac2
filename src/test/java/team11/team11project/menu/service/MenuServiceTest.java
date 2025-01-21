package team11.team11project.menu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import team11.team11project.common.entity.Member;
import team11.team11project.common.entity.Menu;
import team11.team11project.common.entity.Store;
import team11.team11project.common.exception.NotFoundException;
import team11.team11project.menu.model.response.MenuResponse;
import team11.team11project.menu.repository.MenuRepository;
import team11.team11project.mock.MockData;
import team11.team11project.store.repository.StoreRepository;

/**
 * 단위테스트
 */

@ExtendWith(MockitoExtension.class) //MockitoExtension 이라는 애를 확장해서 쓸거다.
class MenuServiceTest {

	// 1-1: 가짜(깡통)객체 만들기. Mock 객체로 있는 척만 해주기.
	@Mock
	private MenuRepository menuRepository;
	@Mock
	private StoreRepository storeRepository;

	// 1-2: 테스트 하려는 실제 객체에 의존성 주입해주기
	@InjectMocks // 만들어둔 @Mock들을 주입할 때 사용.
	private MenuService menuService; //실제 객체

	// 2-1: test code 작성. public은 꼭 있어야한다.
	@Test
	@DisplayName("메뉴 생성 메서드 - 성공 케이스")
	public void createMenu_success() { // 메서드명은 '실제 메서드명_성공/실패여부' 이런식으로도 사용한다.

		// 2-2: 상황 만들기
		// give
		// 외부에서 받아와야하는 createMenu()의 매개변수 임의로 만들기. (실제 사용할 값)
		Long storeId = 1L;
		Long ownerId = 1L;
		String name = "망곰이";
		Integer price = 1000;
		String description = "JMT";
		/*Store store = mock(Store.class); // Store 객체 Mock으로 만들기*/
		/*Member member = mock(Member.class);*/

		// 공유 객체 활용
		Store store = MockData.createStore();
		Member member = MockData.createMember();

		// Mock 상태인 storeRepository가 실제로 동작하는 것이 아니기 때문에 로직상 어떤 값을 받아와야하는 경우 상황 설정을 해줘야한다.
		// storeRepository에서 findById를 하면 Optional 타입의 Store 객체를 반환하겠다.
		when(storeRepository.findById(anyLong()/*혹은 storeId*/)).thenReturn(Optional.of(store)); // 실제 MenuService의 25번 줄에 대한 상황 정의
		/*when(store.getOwner()).thenReturn(member); // 실제 MenuService의 28번 줄에 대한 상황 정의
		when(member.getId()).thenReturn(ownerId); // Member도 결국 가짜 객체이기 때문에, getId()를 했을 때 어떤 값을 반환할 것인지 설정해줘야한다.*/

		Menu menu = new Menu(name, price, description, store, ownerId);
		// 객체 필드 조작. Menu 객체 Id 필드 생성. (id를 만드는 생성자가 현실 코드에 따로 없기 때문에 임의로 조작)
		ReflectionTestUtils.setField(menu, "id", 1L);

		// any()의 역할: 어떤 값이든 상관 없이 지정한 타입이면 OK
		when(menuRepository.save(any(Menu.class))).thenReturn(menu);

		// when
		// 테스트하려는 메서드 호출하기
		MenuResponse result = menuService.createMenu(storeId, ownerId, name, price, description);

		// then
		// 내가 정한 값과 실제로 메서드를 통해 나온 결과 값이 동일한지 확인
		/*assertEquals(result.getId(), menu.getId());*/
		assertEquals(result.getName(), name);
		assertEquals(result.getDescription(), description);
	}

	@Test
	@DisplayName("메뉴 생성 메서드(실패 케이스) - 본인의 가게가 아닌 경우")
	public void createMenu_fail_case_1() {

		// give
		Long storeId = 1L;
		Long ownerId = 1L;
		String name = "망곰이";
		Integer price = 1000;
		String description = "JMT";
		Store store = mock(Store.class);
		Member member = mock(Member.class);

		when(storeRepository.findById(anyLong()/*혹은 storeId*/)).thenReturn(Optional.of(store));
		when(store.getOwner()).thenReturn(member);
		when(member.getId()).thenReturn(2L); // 시나리오상 memberId랑 ownerId를 다르게 설정

		// when & then (Exception에 대한 테스트코인 경우 when과 then이 합쳐지는 상황이 생기기도 한다.)
		// Assertions.assertThrows(희망예외, 예외를 발생시키는 메서드): 결과값에 대해서 어떤 예외를 던졌는지 확일할 수 있는 메서드
		Assertions.assertThrows(NotFoundException.class, () -> {
			menuService.createMenu(storeId, ownerId, name, price, description);
		});
	}
}