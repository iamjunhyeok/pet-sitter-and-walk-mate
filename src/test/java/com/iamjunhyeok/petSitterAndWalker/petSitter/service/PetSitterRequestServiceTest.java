package com.iamjunhyeok.petSitterAndWalker.petSitter.service;

import com.iamjunhyeok.petSitterAndWalker.constants.enums.RequestStatus;
import com.iamjunhyeok.petSitterAndWalker.image.domain.Image;
import com.iamjunhyeok.petSitterAndWalker.pet.domain.Pet;
import com.iamjunhyeok.petSitterAndWalker.pet.repository.PetRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitter;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterOption;
import com.iamjunhyeok.petSitterAndWalker.petSitter.domain.PetSitterRequest;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterRequestDto;
import com.iamjunhyeok.petSitterAndWalker.petSitter.dto.PetSitterResponse;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterOptionRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRepository;
import com.iamjunhyeok.petSitterAndWalker.petSitter.repository.PetSitterRequestRepository;
import com.iamjunhyeok.petSitterAndWalker.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class PetSitterRequestServiceTest {

    @InjectMocks
    private PetSitterRequestService petSitterRequestService;

    @Mock
    private PetSitterRepository petSitterRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetSitterOptionRepository petSitterOptionRepository;

    @Mock
    private PetSitterRequestRepository petSitterRequestRepository;

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);

    private RedissonClient redissonClient;

    @BeforeEach
    void setup() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redis.getHost() + ":" + redis.getFirstMappedPort());
        redissonClient = Redisson.create(config);
    }

    @Test
    @DisplayName("펫 시터에게 보호 요청")
    void testRequestToPetSitter() {
        // Arrange
        List<Long> petIds = Arrays.asList(1L, 2L);
        List<Image> images1 = Arrays.asList(new Image("image1.png"), new Image("image2.png"));
        Pet pet1 = Pet.builder()
                .name("애기")
                .breed("말티즈")
                .build();
        pet1.addImage(images1);
        List<Image> images2 = Arrays.asList(new Image("image3.png"), new Image("image4.png"));
        Pet pet2 = Pet.builder()
                .name("후추")
                .breed("포메라니안")
                .build();
        pet2.addImage(images2);
        List<Pet> pets = Arrays.asList(pet1, pet2);

        List<Long> optionIds = Arrays.asList(3L, 4L);
        List<PetSitterOption> options = Arrays.asList(
                new PetSitterOption("목욕", "목욕시켜줌", 5000),
                new PetSitterOption("발톱정리", "발톱깎아줌", 3000)
        );
        Long petSitterId = 1L;
        PetSitter petSitter = new PetSitter("hello", 5);
        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        PetSitterRequestDto requestDto = new PetSitterRequestDto(petIds, startDate, endDate, optionIds, "please");

        when(petSitterRepository.findById(petSitterId)).thenReturn(Optional.of(petSitter));
        when(petRepository.findAllById(petIds)).thenReturn(pets);
        when(petSitterOptionRepository.findAllById(optionIds)).thenReturn(options);

        // Act
        PetSitterResponse response = petSitterRequestService.requestToPetSitter(requestDto, petSitterId, user);

        // Assert
        assertEquals(startDate, response.getStartDate());
        assertEquals(endDate, response.getEndDate());
        assertEquals(requestDto.getMessage(), response.getMessage());
        assertEquals(pets.size(), response.getPets().size());
        assertEquals(pets.get(0).getName(), response.getPets().get(0).getName());
        assertEquals(options.size(), response.getOptions().size());
        assertEquals(options.get(0).getName(), response.getOptions().get(0).getName());
    }

    @Test
    @DisplayName("펫 시터가 존재하지 않음")
    void testWhenPetSitterNotFound() {
        // Arrange
        Long petSitterId = 1L;
        User user = User.builder()
                .name("사용자")
                .email("jeonjhyeok@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기 성남시")
                .address2("1층")
                .build();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();
        PetSitterRequestDto requestDto = new PetSitterRequestDto(new ArrayList<>(), startDate, endDate, new ArrayList<>(), "please");
        when(petSitterRepository.findById(petSitterId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterRequestService.requestToPetSitter(requestDto, petSitterId, user));
    }

    @Test
    @DisplayName("요청을 수락")
    void testAcceptRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.REQUESTED)
                .petSitter(petSitter)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act
        petSitterRequestService.acceptRequest(petSitterId, requestId, petSitterUser);

        // Assert
        assertEquals(petSitterRequest.getStatus(), RequestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("펫 시터에 대한 요청 정보가 없을 때")
    void testAcceptRequestWhenThereIsNoRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        when(petSitterRequestRepository.findByIdAndPetSitterId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterRequestService.acceptRequest(petSitterId, requestId, requester));
    }

    @Test
    @DisplayName("요청을 수락할 수 없는 사용자")
    void testWhenNotAuthorizedToAcceptRequests() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.REQUESTED)
                .petSitter(petSitter)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> petSitterRequestService.acceptRequest(petSitterId, requestId, requester));
    }

    @Test
    @DisplayName("요청을 수락할 수 없는 상태")
    void testWhenUnableToAcceptRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.CANCELED)
                .petSitter(petSitter)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> petSitterRequestService.acceptRequest(petSitterId, requestId, petSitterUser));
    }

    @Test
    @DisplayName("요청을 거절")
    void testRejectRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.REQUESTED)
                .petSitter(petSitter)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act
        petSitterRequestService.rejectRequest(petSitterId, requestId, petSitterUser);

        // Assert
        assertEquals(petSitterRequest.getStatus(), RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("요청 취소")
    void testCancelRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.REQUESTED)
                .user(requester)
                .petSitter(petSitter)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act
        petSitterRequestService.cancelRequest(petSitterId, requestId, requester);

        // Assert
        assertEquals(petSitterRequest.getStatus(), RequestStatus.CANCELED);
    }

    @Test
    @DisplayName("요청 취소 시, 펫 시터에 대한 요청 정보가 없을 때")
    void testCancelRequestWhenThereIsNoRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        when(petSitterRequestRepository.findByIdAndPetSitterId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petSitterRequestService.cancelRequest(petSitterId, requestId, requester));
    }

    @Test
    @DisplayName("요청을 취소할 수 없는 사용자")
    void testWhenNotAuthorizedToCancelRequests() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.REQUESTED)
                .petSitter(petSitter)
                .user(requester)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> petSitterRequestService.cancelRequest(petSitterId, requestId, petSitterUser));
    }

    @Test
    @DisplayName("요청을 취소할 수 없는 상태")
    void testWhenUnableToCancelRequest() {
        // Arrange
        Long petSitterId = 1L;
        Long requestId = 2L;
        User requester = User.builder()
                .id(1L)
                .name("사용자")
                .email("user@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("경기")
                .address2("1층")
                .build();
        PetSitter petSitter = new PetSitter("hello", 5);
        User petSitterUser = User.builder()
                .id(2L)
                .name("펫시터")
                .email("petSitter@gmail.com")
                .password("1231231")
                .phoneNumber("01012345678")
                .zipCode("12345")
                .address1("서울")
                .address2("1층")
                .build();
        petSitterUser.registerPetSitterInfo(petSitter);
        PetSitterRequest petSitterRequest = PetSitterRequest.builder()
                .id(requestId)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .status(RequestStatus.ACCEPTED)
                .petSitter(petSitter)
                .user(requester)
                .build();
        when(petSitterRequestRepository.findByIdAndPetSitterId(requestId, petSitterId)).thenReturn(Optional.ofNullable(petSitterRequest));

        ReflectionTestUtils.setField(petSitterRequestService, "redissonClient", redissonClient);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> petSitterRequestService.cancelRequest(petSitterId, requestId, requester));
    }
}