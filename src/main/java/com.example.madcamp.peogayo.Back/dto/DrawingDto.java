package com.example.madcamp.peogayo.Back.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrawingDto {
    private String type;
    private float x;
    private float y;
    private String color;
    private float width;
    private String sender;
}