package com.cafein.controller;

import com.cafein.ApiDocumentationTest;
import com.cafein.dto.review.createreview.CreateReviewInput;
import com.cafein.dto.review.updatereview.UpdateReviewInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.cafein.ApiDocumentUtils.getDocumentRequest;
import static com.cafein.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CafeControllerTest extends ApiDocumentationTest {

    @DisplayName("카페 조회")
    @Test
    public void 카페_조회() throws Exception {
        //given
        String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ5LCJpYXQiOjE2MzI4MDgyMDF9.ImwkfxLW84OCWp2hBqYiJzGnZqUO6Ni-GskrZZyoTgM";

        //when
        ResultActions result = mockMvc.perform(get("/api/cafes")
                        .queryParam("latitude", "37.265712")
                        .queryParam("longitude", "127.036734")
                        .queryParam("search", "")
                        .queryParam("size", "10")
                        .queryParam("page", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-ACCESS-TOKEN", JWT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document(
                                "cafes/select/successful",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(headerWithName("X-ACCESS-TOKEN").description("JWT Token").optional()),
                                requestParameters(
                                        parameterWithName("latitude").description("유저 현재 위치 - 위도"),
                                        parameterWithName("longitude").description("유저 현재 위치 - 경도"),
                                        parameterWithName("search").description("카페 검색어"),
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("응답 상태"),
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("page.currentPage").type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("page.pageSize").type(JsonFieldType.NUMBER)
                                                .description("페이지 사이즈"),
                                        fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER)
                                                .description("전체 요소 수"),
                                        fieldWithPath("result.[].cafeId").type(JsonFieldType.NUMBER)
                                                .description("카페 번호"),
                                        fieldWithPath("result.[].cafeName").type(JsonFieldType.STRING)
                                                .description("카페 이름"),
                                        fieldWithPath("result.[].cafeBranch").type(JsonFieldType.STRING)
                                                .description("카페 지점명").optional(),
                                        fieldWithPath("result.[].cafeArea").type(JsonFieldType.STRING)
                                                .description("카페 지역명"),
                                        fieldWithPath("result.[].cafeTel").type(JsonFieldType.STRING)
                                                .description("카페 전화번호").optional(),
                                        fieldWithPath("result.[].cafeAddress").type(JsonFieldType.STRING)
                                                .description("카페 주소"),
                                        fieldWithPath("result.[].cafeLatitude").type(JsonFieldType.STRING)
                                                .description("카페 위도"),
                                        fieldWithPath("result.[].cafeLongitude").type(JsonFieldType.STRING)
                                                .description("카페 경도"),
                                        fieldWithPath("result.[].cafeDistance").type(JsonFieldType.STRING)
                                                .description("현재 위치에서 카페까지 거리"),
                                        fieldWithPath("result.[].imgUrl").type(JsonFieldType.STRING)
                                                .description("카페 이미지").optional(),
                                        fieldWithPath("result.[].isBookMark").type(JsonFieldType.NUMBER)
                                                .description("찜 여부 0 : 찜 x, 1 : 찜 o"),
                                        fieldWithPath("result.[].bhourType").type(JsonFieldType.NUMBER)
                                                .description("영업 시간 종류").optional(),
                                        fieldWithPath("result.[].bhourWeekType").type(JsonFieldType.NUMBER)
                                                .description("주 단위 종류").optional(),
                                        fieldWithPath("result.[].bhourMon").type(JsonFieldType.NUMBER)
                                                .description("월요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourTue").type(JsonFieldType.NUMBER)
                                                .description("화요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourWed").type(JsonFieldType.NUMBER)
                                                .description("수요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourThu").type(JsonFieldType.NUMBER)
                                                .description("목요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourFri").type(JsonFieldType.NUMBER)
                                                .description("금요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourSat").type(JsonFieldType.NUMBER)
                                                .description("토요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourSun").type(JsonFieldType.NUMBER)
                                                .description("일요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.[].bhourStartTime").type(JsonFieldType.STRING)
                                                .description("시작시간").optional(),
                                        fieldWithPath("result.[].bhourEndTime").type(JsonFieldType.STRING)
                                                .description("종료시간").optional(),
                                        fieldWithPath("result.[].bhourEtc").type(JsonFieldType.STRING)
                                                .description("기타정보 ex) 연중휴무").optional(),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("api 호출 일시")
                                )
                        ));
    }

    @DisplayName("카페 상세 조회")
    @Test
    public void 카페_상세_조회() throws Exception {
        //given
        String JWT = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ5LCJpYXQiOjE2MzI4MDgyMDF9.ImwkfxLW84OCWp2hBqYiJzGnZqUO6Ni-GskrZZyoTgM";

        //when
        ResultActions result = mockMvc.perform(get("/api/cafes/{id}", 162)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-ACCESS-TOKEN", JWT)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        result.andExpect(status().isOk())
                .andDo(
                        document(
                                "cafe_detail/select/successful",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(headerWithName("X-ACCESS-TOKEN").description("JWT Token").optional()),
                                pathParameters(
                                        parameterWithName("id").description("조회할 카페 번호")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("응답 상태"),
                                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                                .description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("result.cafeId").type(JsonFieldType.NUMBER)
                                                .description("카페 번호"),
                                        fieldWithPath("result.cafeName").type(JsonFieldType.STRING)
                                                .description("카페 이름"),
                                        fieldWithPath("result.cafeBranch").type(JsonFieldType.STRING)
                                                .description("카페 지점명").optional(),
                                        fieldWithPath("result.cafeArea").type(JsonFieldType.STRING)
                                                .description("카페 지역명"),
                                        fieldWithPath("result.cafeTel").type(JsonFieldType.STRING)
                                                .description("카페 전화번호").optional(),
                                        fieldWithPath("result.cafeAddress").type(JsonFieldType.STRING)
                                                .description("카페 주소"),
                                        fieldWithPath("result.cafeLatitude").type(JsonFieldType.STRING)
                                                .description("카페 위도"),
                                        fieldWithPath("result.cafeLongitude").type(JsonFieldType.STRING)
                                                .description("카페 경도"),
                                        fieldWithPath("result.imgUrl").type(JsonFieldType.STRING)
                                                .description("카페 이미지").optional(),
                                        fieldWithPath("result.isBookMark").type(JsonFieldType.NUMBER)
                                                .description("찜 여부 0 : 찜 x, 1 : 찜 o"),
                                        fieldWithPath("result.bhourType").type(JsonFieldType.NUMBER)
                                                .description("영업 시간 종류").optional(),
                                        fieldWithPath("result.bhourWeekType").type(JsonFieldType.NUMBER)
                                                .description("주 단위 종류").optional(),
                                        fieldWithPath("result.bhourMon").type(JsonFieldType.NUMBER)
                                                .description("월요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourTue").type(JsonFieldType.NUMBER)
                                                .description("화요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourWed").type(JsonFieldType.NUMBER)
                                                .description("수요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourThu").type(JsonFieldType.NUMBER)
                                                .description("목요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourFri").type(JsonFieldType.NUMBER)
                                                .description("금요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourSat").type(JsonFieldType.NUMBER)
                                                .description("토요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourSun").type(JsonFieldType.NUMBER)
                                                .description("일요일 포함유무 1 - 포함, 0 - 미포함").optional(),
                                        fieldWithPath("result.bhourStartTime").type(JsonFieldType.STRING)
                                                .description("시작시간").optional(),
                                        fieldWithPath("result.bhourEndTime").type(JsonFieldType.STRING)
                                                .description("종료시간").optional(),
                                        fieldWithPath("result.bhourEtc").type(JsonFieldType.STRING)
                                                .description("기타정보 ex) 연중휴무").optional(),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("api 호출 일시")
                                )
                        ));
    }

}