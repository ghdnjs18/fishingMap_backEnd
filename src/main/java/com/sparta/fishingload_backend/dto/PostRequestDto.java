package com.sparta.fishingload_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String contents;
    private String fishtype;
    private String locationdate;
    private List<Double> coordinates;
    private Long categoryId;
}
