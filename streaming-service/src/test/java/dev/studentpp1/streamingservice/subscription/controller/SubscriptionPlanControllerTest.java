package dev.studentpp1.streamingservice.subscription.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.auth.persistence.AuthenticatedUser;
import dev.studentpp1.streamingservice.common.config.GlobalExceptionHandler;
import dev.studentpp1.streamingservice.subscription.dto.request.CreateSubscriptionPlanRequest;
import dev.studentpp1.streamingservice.subscription.dto.response.SubscriptionPlanDetailsDto;
import dev.studentpp1.streamingservice.subscription.dto.response.SubscriptionPlanSummaryDto;
import dev.studentpp1.streamingservice.subscription.entity.SubscriptionPlan;
import dev.studentpp1.streamingservice.subscription.exception.SubscriptionPlanNotFoundException;
import dev.studentpp1.streamingservice.subscription.mapper.SubscriptionPlanMapper;
import dev.studentpp1.streamingservice.subscription.service.SubscriptionPlanService;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SubscriptionPlanControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private SubscriptionPlanService subscriptionPlanService;

    @Mock
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @InjectMocks
    private SubscriptionPlanController subscriptionPlanController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionPlanController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    private AppUser createTestUser() {
        return AppUser.builder()
            .id(1L)
            .email("admin@test.com")
            .name("Test")
            .surname("Admin")
            .birthday(LocalDate.of(1990, 1, 1))
            .build();
    }

    private AuthenticatedUser createAuthenticatedUser() {
        AppUser appUser = createTestUser();
        return new AuthenticatedUser(appUser);
    }

    private SubscriptionPlan createTestPlan(Long id, String name, BigDecimal price) {
        return SubscriptionPlan.builder()
            .id(id)
            .name(name)
            .description(name + " plan")
            .price(price)
            .duration(30)
            .build();
    }

    private SubscriptionPlanSummaryDto createSummaryDto(Long id, String name, BigDecimal price) {
        return new SubscriptionPlanSummaryDto(
            id,
            name,
            name + " plan",
            price,
            30
        );
    }

    private SubscriptionPlanDetailsDto createDetailsDto(Long id, String name, BigDecimal price) {
        return new SubscriptionPlanDetailsDto(
            id,
            name,
            name + " plan",
            price,
            30,
            Collections.emptySet());
    }

    private CreateSubscriptionPlanRequest createValidRequest() {
        return new CreateSubscriptionPlanRequest(
            "Premium Plan",
            "Premium subscription plan",
            new BigDecimal("19.99"),
            30,
            null);
    }

    // GET /api/subscription-plans

    @Test
    void getAllPlans_withoutSearch_returnsPageOfPlans() throws Exception {
        SubscriptionPlan plan1 = createTestPlan(1L, "Basic", new BigDecimal("9.99"));
        SubscriptionPlan plan2 = createTestPlan(2L, "Premium", new BigDecimal("19.99"));

        SubscriptionPlanSummaryDto dto1 = createSummaryDto(1L, "Basic", new BigDecimal("9.99")
        );
        SubscriptionPlanSummaryDto dto2 = createSummaryDto(2L, "Premium", new BigDecimal("19.99")
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));
        Page<SubscriptionPlan> planPage = new PageImpl<>(List.of(plan1, plan2), pageable, 2);

        when(subscriptionPlanService.getAllPlans(eq(null), any(Pageable.class)))
            .thenReturn(planPage);
        when(subscriptionPlanMapper.toSummaryDto(plan1)).thenReturn(dto1);
        when(subscriptionPlanMapper.toSummaryDto(plan2)).thenReturn(dto2);

        mockMvc.perform(get("/api/subscription-plans"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id", is(1)))
            .andExpect(jsonPath("$.content[0].name", is("Basic")))
            .andExpect(jsonPath("$.content[1].id", is(2)))
            .andExpect(jsonPath("$.content[1].name", is("Premium")))
            .andExpect(jsonPath("$.totalElements", is(2)));

        verify(subscriptionPlanService).getAllPlans(eq(null), any(Pageable.class));
    }

    @Test
    void getAllPlans_withSearch_returnsFilteredPlans() throws Exception {
        SubscriptionPlan plan = createTestPlan(1L, "Premium", new BigDecimal("19.99"));
        SubscriptionPlanSummaryDto dto = createSummaryDto(1L, "Premium", new BigDecimal("19.99")
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));
        Page<SubscriptionPlan> planPage = new PageImpl<>(List.of(plan), pageable, 1);

        when(subscriptionPlanService.getAllPlans(eq("Premium"), any(Pageable.class)))
            .thenReturn(planPage);
        when(subscriptionPlanMapper.toSummaryDto(plan)).thenReturn(dto);

        mockMvc.perform(get("/api/subscription-plans")
                .param("search", "Premium"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("Premium")));

        verify(subscriptionPlanService).getAllPlans(eq("Premium"), any(Pageable.class));
    }

    @Test
    void getAllPlans_whenNoResults_returnsEmptyPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));
        Page<SubscriptionPlan> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(subscriptionPlanService.getAllPlans(eq(null), any(Pageable.class)))
            .thenReturn(emptyPage);

        mockMvc.perform(get("/api/subscription-plans"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)))
            .andExpect(jsonPath("$.totalElements", is(0)));

        verify(subscriptionPlanService).getAllPlans(eq(null), any(Pageable.class));
    }

    // GET /api/subscription-plans/{id}

    @Test
    void getPlanById_withValidId_returnsPlanDetails() throws Exception {
        Long planId = 1L;
        SubscriptionPlan plan = createTestPlan(planId, "Premium", new BigDecimal("19.99"));
        SubscriptionPlanDetailsDto dto = createDetailsDto(planId, "Premium",
            new BigDecimal("19.99"));

        when(subscriptionPlanService.getPlanById(planId)).thenReturn(plan);
        when(subscriptionPlanMapper.toDetailsDto(plan)).thenReturn(dto);

        mockMvc.perform(get("/api/subscription-plans/{id}", planId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Premium")))
            .andExpect(jsonPath("$.price", is(19.99)))
            .andExpect(jsonPath("$.duration", is(30)));

        verify(subscriptionPlanService).getPlanById(planId);
        verify(subscriptionPlanMapper).toDetailsDto(plan);
    }

    @Test
    void getPlanById_whenNotFound_returnsNotFound() throws Exception {
        Long planId = 999L;

        when(subscriptionPlanService.getPlanById(planId))
            .thenThrow(new SubscriptionPlanNotFoundException(planId));

        mockMvc.perform(get("/api/subscription-plans/{id}", planId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionPlanService).getPlanById(planId);
    }

    // POST /api/subscription-plans

    @Test
    void createPlan_withValidRequest_returnsCreatedPlan() throws Exception {
        CreateSubscriptionPlanRequest request = createValidRequest();
        SubscriptionPlan createdPlan = createTestPlan(1L, "Premium Plan",
            new BigDecimal("19.99"));
        SubscriptionPlanDetailsDto dto = createDetailsDto(1L, "Premium Plan",
            new BigDecimal("19.99"));
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.createPlan(any(CreateSubscriptionPlanRequest.class)))
            .thenReturn(createdPlan);
        when(subscriptionPlanMapper.toDetailsDto(createdPlan)).thenReturn(dto);

        mockMvc.perform(post("/api/subscription-plans")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Premium Plan")))
            .andExpect(jsonPath("$.price", is(19.99)));

        verify(subscriptionPlanService).createPlan(any(CreateSubscriptionPlanRequest.class));
    }

    @Test
    void createPlan_withBlankName_returnsBadRequest() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "",
            "Description",
            new BigDecimal("19.99"),
            30,
            null);
        AuthenticatedUser admin = createAuthenticatedUser();

        mockMvc.perform(post("/api/subscription-plans")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionPlanService, never()).createPlan(any());
    }

    @Test
    void createPlan_withNullPrice_returnsBadRequest() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "Premium Plan",
            "Description",
            null,
            30,
            null);
        AuthenticatedUser admin = createAuthenticatedUser();

        mockMvc.perform(post("/api/subscription-plans")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionPlanService, never()).createPlan(any());
    }

    @Test
    void createPlan_withNegativePrice_returnsBadRequest() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "Premium Plan",
            "Description",
            new BigDecimal("-10.00"),
            30,
            null);
        AuthenticatedUser admin = createAuthenticatedUser();

        mockMvc.perform(post("/api/subscription-plans")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionPlanService, never()).createPlan(any());
    }

    @Test
    void createPlan_withZeroDuration_returnsBadRequest() throws Exception {
        CreateSubscriptionPlanRequest request = new CreateSubscriptionPlanRequest(
            "Premium Plan",
            "Description",
            new BigDecimal("19.99"),
            0,
            null
        );
        AuthenticatedUser admin = createAuthenticatedUser();

        mockMvc.perform(post("/api/subscription-plans")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));

        verify(subscriptionPlanService, never()).createPlan(any());
    }

    // PUT /api/subscription-plans/{id}

    @Test
    void updatePlan_withValidRequest_returnsUpdatedPlan() throws Exception {
        Long planId = 1L;
        CreateSubscriptionPlanRequest request = createValidRequest();
        SubscriptionPlan updatedPlan = createTestPlan(planId, "Premium Plan",
            new BigDecimal("19.99"));
        SubscriptionPlanDetailsDto dto = createDetailsDto(planId, "Premium Plan",
            new BigDecimal("19.99"));
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.updatePlan(eq(planId),
            any(CreateSubscriptionPlanRequest.class)))
            .thenReturn(updatedPlan);
        when(subscriptionPlanMapper.toDetailsDto(updatedPlan)).thenReturn(dto);

        mockMvc.perform(put("/api/subscription-plans/{id}", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Premium Plan")));

        verify(subscriptionPlanService).updatePlan(eq(planId),
            any(CreateSubscriptionPlanRequest.class));
    }

    @Test
    void updatePlan_whenNotFound_returnsNotFound() throws Exception {
        Long planId = 999L;
        CreateSubscriptionPlanRequest request = createValidRequest();
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.updatePlan(eq(planId),
            any(CreateSubscriptionPlanRequest.class)))
            .thenThrow(new SubscriptionPlanNotFoundException(planId));

        mockMvc.perform(put("/api/subscription-plans/{id}", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionPlanService).updatePlan(eq(planId),
            any(CreateSubscriptionPlanRequest.class));
    }

    // POST /api/subscription-plans/{id}/movies

    @Test
    void addMoviesToPlan_withValidMovieIds_returnsUpdatedPlan() throws Exception {
        Long planId = 1L;
        List<Long> movieIds = List.of(1L, 2L, 3L);
        SubscriptionPlan updatedPlan = createTestPlan(planId, "Premium", new BigDecimal("19.99")
        );
        SubscriptionPlanDetailsDto dto = createDetailsDto(planId, "Premium",
            new BigDecimal("19.99"));
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.addMoviesToPlan(planId, movieIds)).thenReturn(updatedPlan);
        when(subscriptionPlanMapper.toDetailsDto(updatedPlan)).thenReturn(dto);

        mockMvc.perform(post("/api/subscription-plans/{id}/movies", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieIds)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("Premium")));

        verify(subscriptionPlanService).addMoviesToPlan(planId, movieIds);
    }

    @Test
    void addMoviesToPlan_whenPlanNotFound_returnsNotFound() throws Exception {
        Long planId = 999L;
        List<Long> movieIds = List.of(1L, 2L);
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.addMoviesToPlan(planId, movieIds))
            .thenThrow(new SubscriptionPlanNotFoundException(planId));

        mockMvc.perform(post("/api/subscription-plans/{id}/movies", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieIds)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionPlanService).addMoviesToPlan(planId, movieIds);
    }

    // DELETE /api/subscription-plans/{id}/movies

    @Test
    void removeMoviesFromPlan_withValidMovieIds_returnsUpdatedPlan() throws Exception {
        Long planId = 1L;
        List<Long> movieIds = List.of(1L, 2L);
        SubscriptionPlan updatedPlan = createTestPlan(planId, "Premium", new BigDecimal("19.99")
        );
        SubscriptionPlanDetailsDto dto = createDetailsDto(planId, "Premium",
            new BigDecimal("19.99"));
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.removeMoviesFromPlan(planId, movieIds))
            .thenReturn(updatedPlan);
        when(subscriptionPlanMapper.toDetailsDto(updatedPlan)).thenReturn(dto);

        mockMvc.perform(delete("/api/subscription-plans/{id}/movies", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieIds)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));

        verify(subscriptionPlanService).removeMoviesFromPlan(planId, movieIds);
    }

    @Test
    void removeMoviesFromPlan_whenPlanNotFound_returnsNotFound() throws Exception {
        Long planId = 999L;
        List<Long> movieIds = List.of(1L);
        AuthenticatedUser admin = createAuthenticatedUser();

        when(subscriptionPlanService.removeMoviesFromPlan(planId, movieIds))
            .thenThrow(new SubscriptionPlanNotFoundException(planId));

        mockMvc.perform(delete("/api/subscription-plans/{id}/movies", planId)
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movieIds)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionPlanService).removeMoviesFromPlan(planId, movieIds);
    }

    // DELETE /api/subscription-plans/{id}

    @Test
    void deletePlan_withValidId_returnsNoContent() throws Exception {
        Long planId = 1L;
        AuthenticatedUser admin = createAuthenticatedUser();

        mockMvc.perform(delete("/api/subscription-plans/{id}", planId)
                .with(user(admin)))
            .andExpect(status().isNoContent());

        verify(subscriptionPlanService).deletePlan(planId);
    }

    @Test
    void deletePlan_whenNotFound_returnsNotFound() throws Exception {
        Long planId = 999L;
        AuthenticatedUser admin = createAuthenticatedUser();

        doThrow(new SubscriptionPlanNotFoundException(planId))
            .when(subscriptionPlanService).deletePlan(planId);

        mockMvc.perform(delete("/api/subscription-plans/{id}", planId)
                .with(user(admin)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));

        verify(subscriptionPlanService).deletePlan(planId);
    }
}
