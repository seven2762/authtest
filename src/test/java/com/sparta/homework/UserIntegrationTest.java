package com.sparta.homework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.homework.domain.model.UserRole;
import com.sparta.homework.dto.LoginReq;
import com.sparta.homework.dto.LoginRes;
import com.sparta.homework.dto.UserCreateRequest;
import com.sparta.homework.dto.UserResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class UserIntegrationTest {

    private static String adminToken;
    private static String userToken;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("일반 사용자 회원가입")
    @Order(1)
    public void testSignUpUser() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        IntStream.range(0, 2).forEach(i -> executorService.execute(() -> {

            try {
                //given
                UserCreateRequest request = new UserCreateRequest("testuser", "password123",
                    "tester");
                String requestJson = objectMapper.writeValueAsString(request);

                //when
                MvcResult result = mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userName").value("testuser"))
                    .andExpect(jsonPath("$.data.nickName").value("tester"))
                    .andExpect(jsonPath("$.data.roles.role").value("USER"))
                    .andReturn();

                //then
                String responseBody = result.getResponse().getContentAsString();
                System.out.println("responseBody: " + responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                UserResponse res = objectMapper.treeToValue(jsonNode.get("data"),
                    UserResponse.class);
                System.out.println("res: " + res);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();
            }

        }));
        countDownLatch.await();
    }
    @Test
    @DisplayName("일반 유저 로그인")
    @Order(2)
    public void LoginUser() throws Exception {

        //given

        LoginReq request = new LoginReq("testuser", "password123");
        String requestJson = objectMapper.writeValueAsString(request);

        //when

        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"))
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.role").value("USER"))
            .andReturn();

        //then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("responseBody: " + responseBody);

        userToken = result.getResponse().getHeader("Authorization");
        System.out.println("Authorization token: " + userToken);

        LoginRes res = objectMapper.readValue(responseBody, LoginRes.class);
        System.out.println("res: " + res);
    }

    @Test
    @DisplayName("비밀번호 불일치 예외 테스트")
    @Order(3)
    public void testLoginInvalidPasswordUser() throws Exception {

        //given

        LoginReq request = new LoginReq("testuser", "password124");
        String requestJson = objectMapper.writeValueAsString(request);

        //when

        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isNotFound())
            .andReturn();

        //then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("responseBody: " + responseBody);

        LoginRes res = objectMapper.readValue(responseBody, LoginRes.class);
        System.out.println("res: " + res);
    }
    @Test
    @Order(4)
    @DisplayName("일반 사용자가 관리자 권한 부여") // 권한별 접근 제어 테스트
    public void testRegularUserCannotAssignAdminRole() throws Exception {

        String tokenValue = userToken;
        if (tokenValue != null && tokenValue.startsWith("Bearer ")) {
            tokenValue = tokenValue.substring(7);
        }

        //given
        Long userId = 1L;
        UserRole userRole = UserRole.USER;

        String requestJson = objectMapper.writeValueAsString(userRole);

        //when

        MvcResult result = mockMvc.perform(patch("/admin/users/{userId}/roles", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer " + tokenValue)
                .content(requestJson))
            .andExpect(status().isForbidden())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Forbidden response body: " + responseBody);

        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if (jsonNode.has("code")) {
                    int errorCode = jsonNode.get("code").asInt();
                    String errorMessage = jsonNode.has("message") ?
                        jsonNode.get("message").asText() : "";
                    System.out.println("Error code: " + errorCode + ", message: " + errorMessage);
                }
            } catch (Exception e) {
                System.out.println("Failed to parse error response: " + e.getMessage());
            }
        }

    }
    @Test
    @DisplayName("관리자 생성 테스트")
    @Order(5)
    public void testSignUpAdmin() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        IntStream.range(0, 2).forEach(i -> executorService.execute(() ->{

            try {

                UserCreateRequest request = new UserCreateRequest("testadmin", "password123",
                    "admin");
                String requestJson = objectMapper.writeValueAsString(request);
                System.out.println("requestJson: " + requestJson);
                //when
                MvcResult result = mockMvc.perform(post("/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userName").value("testadmin"))
                    .andExpect(jsonPath("$.data.nickName").value("admin"))
                    .andExpect(jsonPath("$.data.roles.role").value("ADMIN"))
                    .andReturn();

                //then
                String responseBody = result.getResponse().getContentAsString();
                System.out.println("responseBody: " + responseBody);

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                UserResponse res = objectMapper.treeToValue(jsonNode.get("data"),
                    UserResponse.class);
                System.out.println("res: " + res);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();
    }

    @Test
    @DisplayName("관리자 유저 로그인")
    @Order(6)
    public void LoginAdmin() throws Exception {

        //given

        LoginReq request = new LoginReq("testadmin", "password123");
        String requestJson = objectMapper.writeValueAsString(request);

        //when

        MvcResult result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(header().exists("Authorization"))
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.role").value("ADMIN"))
            .andReturn();

        //then
        String responseBody = result.getResponse().getContentAsString();
        adminToken = result.getResponse().getHeader("Authorization");
        System.out.println("adminToken: " + adminToken);
        System.out.println("responseBody: " + responseBody);

    }
    @Test
    @Order(7)
    @DisplayName("일반 유저 관리자로 변경")
    public void updateUserRole() throws Exception {

        String tokenValue = adminToken;
        if (tokenValue != null && tokenValue.startsWith("Bearer ")) {
            tokenValue = tokenValue.substring(7);
        }

        //given
        Long userId = 1L;
        UserRole userRole = UserRole.ADMIN;

        String requestJson = objectMapper.writeValueAsString(userRole);

        //when

        MvcResult result = mockMvc.perform(patch("/admin/users/{userId}/roles", userId)
            .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer " + tokenValue)
            .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.userName").value("testuser"))
            .andExpect(jsonPath("$.data.nickName").value("tester"))
            .andExpect(jsonPath("$.data.roles.role").value("ADMIN"))
            .andReturn();

        //then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Update responseBody: " + responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        UserResponse res = objectMapper.treeToValue(jsonNode.get("data"), UserResponse.class);
        System.out.println("res: " + res);

    }
}