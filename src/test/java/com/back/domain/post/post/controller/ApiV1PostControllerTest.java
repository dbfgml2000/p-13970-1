package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc // MockMvc를 자동으로 설정. 컨트롤러 테스트에만 붙이면 됨
@Transactional
class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("글 작성")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": "내용"
                                        }
                                        """)
                ).andDo(print());

        Post post = postService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("msg").value("%d번 글이 작성되었습니다.".formatted(post.getId())))
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.createdDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.title").value("제목"))
                .andExpect(jsonPath("$.data.content").value("내용"));
    }

    @Test
    @DisplayName("글 쓰기, without title")
    void t7() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "content": "내용"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("msg").value("""
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 100
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 쓰기, without content")
    void t8() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목",
                                            "content": ""
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("msg").value("""
                        content-NotBlank-must not be blank
                        content-Size-size must be between 2 and 5000
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("글 쓰기, with wrong json syntax")
    void t9() throws Exception {
        String wrongJsonBody = """
                {
                    "title": "제목",
                    content": "내용"
                """;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(wrongJsonBody)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("msg").value("요청 본문이 올바르지 않습니다.".stripIndent().trim()));
    }

    @Test
    @DisplayName("글 수정")
    void t2() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("msg").value("%d번 글이 수정되었습니다.".formatted(id)));

        // 컨트롤러 테스트는 컨트롤러가 응답한 결과를 검증하는 것이므로,
        // 아래와 같이 수정한 post를 불러와 assertThat으로
        // 서비스 로직이 담당하는 부분까지 검증하는 것은 선택
        // Post post = postService.findById(id).get();
    }


    @Test
    @DisplayName("글 삭제")
    void t3() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("msg").value("%d번 게시글이 삭제되었습니다.".formatted(id)));
    }

    @Test
    @DisplayName("글 단건 조회")
    void t4() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                ).andDo(print());

        Post post = postService.findById(id).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.createdDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.modifiedDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));

        /*
        // 이렇게 각 JSON 필드에 올바른 형식의 데이터가 들어있는지만 체크해도 OK
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdDate").isString())
                .andExpect(jsonPath("$.modifiedDate").isString())
                .andExpect(jsonPath("$.content").isString())
                .andExpect(jsonPath("$.title").isString());
         */

    }

    @Test
    @DisplayName("글 단건 조회, 404")
    void t6() throws Exception {
        int id = Integer.MAX_VALUE;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("글 다건 조회")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts")
                ).andDo(print());

        List<Post> posts = postService.findAll();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(posts.size()));

        /*
        // posts.size()번째 인덱스가 존재하지 않는지 확인
        resultActions
                .andExpect(jsonPath("$[%d]".formatted(posts.size())).doesNotExist());

        // 아래와 같이 전체가 배열인지, 비어있지 않은지, 크기가 posts.size()인지 확인할 수도 있음
        resultActions
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", Matchers.hasSize(posts.size())));
         */

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$[%d].createdDate".formatted(i)).value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifiedDate".formatted(i)).value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }
    }
}