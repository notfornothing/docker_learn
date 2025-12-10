package org.example.controller;

import org.example.service.MarkdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
public class MarkdownController {

    @Autowired
    private MarkdownService markdownService;

    /**
     * 获取文档列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> listDocs() {
        return ResponseEntity.ok(markdownService.listMarkdownFiles());
    }

    /**
     * 获取文档内容（Markdown 原始内容）
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Map<String, Object>> getDoc(@PathVariable String filename) {
        Map<String, Object> result = new HashMap<>();
        try {
            String markdown = markdownService.readRawMarkdown(filename);
            String html = markdownService.markdownToHtml(markdown);
            
            result.put("filename", filename);
            result.put("markdown", markdown);
            result.put("html", html);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("error", "文件不存在: " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * 创建或更新文档
     */
    @PostMapping("/{filename:.+}")
    public ResponseEntity<Map<String, Object>> saveDoc(
            @PathVariable String filename,
            @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String content = request.get("content");
            if (content == null) {
                result.put("success", false);
                result.put("message", "content 字段不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            markdownService.saveMarkdownFile(filename, content);
            result.put("success", true);
            result.put("message", "文档保存成功");
            result.put("filename", filename);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<Map<String, Object>> deleteDoc(@PathVariable String filename) {
        Map<String, Object> result = new HashMap<>();
        try {
            markdownService.deleteMarkdownFile(filename);
            result.put("success", true);
            result.put("message", "文档删除成功");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 将 Markdown 转换为 HTML
     */
    @PostMapping("/convert")
    public ResponseEntity<Map<String, String>> convertMarkdown(@RequestBody Map<String, String> request) {
        Map<String, String> result = new HashMap<>();
        String markdown = request.get("markdown");
        if (markdown == null) {
            result.put("error", "markdown 字段不能为空");
            return ResponseEntity.badRequest().body(result);
        }
        
        String html = markdownService.markdownToHtml(markdown);
        result.put("html", html);
        return ResponseEntity.ok(result);
    }
}
