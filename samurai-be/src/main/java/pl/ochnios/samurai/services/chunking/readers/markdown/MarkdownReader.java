/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.ochnios.samurai.services.chunking.readers.markdown;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.markdown.MarkdownRenderer;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import pl.ochnios.samurai.services.chunking.exception.ChunkingException;

/**
 * Reads the given Markdown resource and groups headers, paragraphs, or text divided by
 * horizontal lines (depending on the
 * {@link MarkdownReaderConfig} configuration) into
 * {@link Document}s.
 *
 * @author Piotr Olaszewski, Szymon Ochnio
 */
public class MarkdownReader implements DocumentReader {

    /**
     * The resource points to the Markdown document.
     */
    private final Resource markdownResource;

    /**
     * Configuration to a parsing process.
     */
    private final MarkdownReaderConfig config;

    /**
     * Markdown parser.
     */
    private final Parser parser;

    public MarkdownReader(String markdownContent) {
        this(new ByteArrayResource(markdownContent.getBytes(StandardCharsets.UTF_8)), defaultConfig());
    }

    public MarkdownReader(Resource markdownResource) {
        this(markdownResource, defaultConfig());
    }

    public MarkdownReader(String markdownContent, MarkdownReaderConfig config) {
        this(new ByteArrayResource(markdownContent.getBytes(StandardCharsets.UTF_8)), config);
    }

    public MarkdownReader(Resource markdownResource, MarkdownReaderConfig config) {
        this.markdownResource = markdownResource;
        this.config = config;
        this.parser = Parser.builder().build();
    }

    public static MarkdownReaderConfig defaultConfig() {
        return MarkdownReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withMaxChunkLength(4000)
                .withMinChunkLength(350)
                .build();
    }

    /**
     * Extracts and returns a list of documents from the resource.
     * @return List of extracted {@link Document}
     */
    @Override
    public List<Document> get() {
        try (var input = markdownResource.getInputStream()) {
            var node = parser.parseReader(new InputStreamReader(input));

            var documentVisitor = new DocumentVisitor(config);
            node.accept(documentVisitor);

            return documentVisitor.getDocuments();
        } catch (IOException ex) {
            throw new ChunkingException(
                    "Failed to extract document content from " + markdownResource.getFilename(), ex);
        }
    }

    /**
     * A convenient class for visiting handled nodes in the Markdown document.
     * Document is split semantically between headers if it is possible or
     * always when the maxChunkLength is reached.
     */
    static class DocumentVisitor extends AbstractVisitor {

        private final MarkdownReaderConfig config;
        private final MarkdownRenderer renderer = new MarkdownRenderer.Builder().build();
        private final List<Document> documents = new ArrayList<>();
        private final Map<Integer, String> headerHierarchy = new HashMap<>();

        private StringBuilder currentContent = new StringBuilder();
        private boolean hasContent = false;
        private int currentHeaderLevel = 0;

        DocumentVisitor(MarkdownReaderConfig config) {
            this.config = config;
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            if (!config.includeBlockquote) {
                buildAndFlush();
            }

            insertHardBreak();
            addContent(blockQuote);
            insertHardBreak();
        }

        @Override
        public void visit(BulletList bulletList) {
            insertHardBreak();
            addContent(bulletList);
            insertHardBreak();
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            if (!config.includeCodeBlock) {
                buildAndFlush();
            }

            insertHardBreak();
            addContent(fencedCodeBlock);
            insertHardBreak();
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            insertHardBreak();
        }

        @Override
        public void visit(Heading heading) {
            currentHeaderLevel = heading.getLevel();
            if (shouldSplitSemantically()) {
                buildAndFlush();
            }

            insertHardBreak();
            var renderedHeader = renderer.render(heading);
            headerHierarchy.put(currentHeaderLevel, renderedHeader);
            currentContent.append(renderedHeader);
            insertHardBreak();
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            if (config.horizontalRuleCreateDocument) {
                buildAndFlush();
            }
        }

        @Override
        public void visit(HtmlBlock htmlBlock) {
            insertHardBreak();
            addContent(htmlBlock);
            insertHardBreak();
        }

        @Override
        public void visit(OrderedList orderedList) {
            insertHardBreak();
            addContent(orderedList);
            insertHardBreak();
        }

        @Override
        public void visit(Paragraph paragraph) {
            insertHardBreak();
            var paragraphContent = renderer.render(paragraph).replace('\n', ' ');
            hasContent = true;
            currentContent.append(paragraphContent);
            insertHardBreak();
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            currentContent.append(' ');
        }

        public List<Document> getDocuments() {
            buildAndFlush();
            return documents;
        }

        private void buildAndFlush() {
            if (!currentContent.isEmpty()) {
                var builder =
                        Document.builder().withContent(currentContent.toString().trim());
                config.additionalMetadata.forEach(builder::withMetadata);
                documents.add(builder.build());

                hasContent = false;
                currentContent = new StringBuilder();
                rebuildHeaderHierarchy(currentContent, currentHeaderLevel);
            }
        }

        private void addContent(Node node) {
            if (currentContent.length() > config.maxChunkLength) {
                buildAndFlush();
            }

            String content = renderer.render(node);
            if (node instanceof Paragraph) {
                content = content.replace('\n', ' ');
            }
            currentContent.append(content);
            hasContent = true;
        }

        private boolean shouldSplitSemantically() {
            return currentContent.length() > config.maxChunkLength
                    || (currentContent.length() >= config.minChunkLength
                            && (headerHierarchy.containsKey(currentHeaderLevel) || hasContent));
        }

        private void rebuildHeaderHierarchy(StringBuilder builder, int untilLevel) {
            for (int i = 1; i < untilLevel; i++) {
                String headerText = headerHierarchy.get(i);
                if (headerText != null) {
                    builder.append(headerText).append("\n\n");
                }
            }
        }

        private void insertHardBreak() {
            int len = currentContent.length();
            if (len > 1) {
                String suffix = "";
                if (currentContent.charAt(len - 2) != '\n') {
                    suffix += "\n";
                }
                if (currentContent.charAt(len - 1) != '\n') {
                    suffix += "\n";
                }
                currentContent.append(suffix);
            }
        }
    }
}
