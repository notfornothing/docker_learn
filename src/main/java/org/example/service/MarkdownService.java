package org.example.service;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MarkdownService {

    private static final String DOCS_DIR = "docs";
    
    // 启用表格扩展（GitHub风格）
    private static final List<Extension> EXTENSIONS = Arrays.asList(TablesExtension.create());
    private final Parser parser = Parser.builder()
            .extensions(EXTENSIONS)
            .build();
    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(EXTENSIONS)
            .build();

    /**
     * 将 Markdown 文本转换为 HTML
     */
    public String markdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * 读取 Markdown 文件并转换为 HTML
     */
    public String readMarkdownFile(String filename) throws IOException {
        Path filePath = Paths.get(DOCS_DIR, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filename);
        }
        String markdown = Files.readString(filePath);
        return markdownToHtml(markdown);
    }

    /**
     * 获取所有 Markdown 文件列表
     */
    public List<String> listMarkdownFiles() {
        try {
            Path docsPath = Paths.get(DOCS_DIR);
            if (!Files.exists(docsPath)) {
                Files.createDirectories(docsPath);
                return new ArrayList<>();
            }

            try (Stream<Path> paths = Files.walk(docsPath)) {
                return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .map(path -> docsPath.relativize(path).toString())
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 保存 Markdown 文件
     */
    public void saveMarkdownFile(String filename, String content) throws IOException {
        Path docsPath = Paths.get(DOCS_DIR);
        if (!Files.exists(docsPath)) {
            Files.createDirectories(docsPath);
        }
        Path filePath = docsPath.resolve(filename);
        Files.writeString(filePath, content);
    }

    /**
     * 读取原始 Markdown 内容
     */
    public String readRawMarkdown(String filename) throws IOException {
        Path filePath = Paths.get(DOCS_DIR, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filename);
        }
        return Files.readString(filePath);
    }

    /**
     * 删除 Markdown 文件
     */
    public void deleteMarkdownFile(String filename) throws IOException {
        Path filePath = Paths.get(DOCS_DIR, filename);
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filename);
        }
        Files.delete(filePath);
    }
}

