package com.example.spring.rmago.swagger;

import com.example.spring.rmago.dto.RadiusRequestDto;
import com.example.spring.rmago.dto.RadiusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface TaxiControllerDocs {

    @Operation(
            summary = "금액 기준 도달 반경 계산",
            description = "입력한 택시 요금(₩)을 기준으로 주간 중형 택시 기준 도달 가능한 반경(미터)을 계산합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RadiusRequestDto.class),
                            examples = @ExampleObject(
                                    name = "예시 요청",
                                    value = "{ \"fare\": 10000 }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상적으로 반경 계산 완료",
                            content = @Content(
                                    schema = @Schema(implementation = RadiusResponseDto.class),
                                    examples = @ExampleObject(
                                            name = "예시 응답",
                                            value = "{ \"radiusInMeters\": 2150.0 }"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<RadiusResponseDto> estimateRadius(RadiusRequestDto request);
}
