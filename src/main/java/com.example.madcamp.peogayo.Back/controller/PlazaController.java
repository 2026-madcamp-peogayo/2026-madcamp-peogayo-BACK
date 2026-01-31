package com.example.madcamp.peogayo.Back.controller;

import com.example.madcamp.peogayo.Back.dto.DrawingDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class PlazaController {

    private final List<DrawingDto> drawingHistory = new CopyOnWriteArrayList<>();

    // 1. 실시간 드로잉 (WebSocket)
    @MessageMapping("/plaza/draw")
    @SendTo("/topic/plaza")
    public DrawingDto broadcastDrawing(DrawingDto message) {
        // 1. 서버 메모리에 저장
        drawingHistory.add(message);

        // 2. 로그 찍기
        System.out.println("광장 드로잉: " + message.getSender());

        // 3. 접속 중인 사람들에게 리턴
        return message;
    }

    // 2. 입장 시 과거 기록 불러오기 (HTTP API)
    @GetMapping("/api/plaza/history")
    public ResponseEntity<List<DrawingDto>> getDrawingHistory() {
        return ResponseEntity.ok(drawingHistory);
    }

    // 3. 광장 지우기
    @PostMapping("/api/plaza/clear")
    public ResponseEntity<String> clearPlaza() {
        drawingHistory.clear();
        return ResponseEntity.ok("광장이 초기화되었습니다.");
    }
}