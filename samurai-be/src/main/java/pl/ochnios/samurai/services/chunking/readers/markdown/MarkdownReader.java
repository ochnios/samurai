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
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;
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
     */
    static class DocumentVisitor extends AbstractVisitor {

        private final MarkdownReaderConfig config;
        private final List<Document> documents = new ArrayList<>();
        private final Map<Integer, String> headerHierarchy = new HashMap<>();

        private StringBuilder currentContent = new StringBuilder();
        private boolean hasTextContent = false;
        private int currentHeaderLevel = 0;

        DocumentVisitor(MarkdownReaderConfig config) {
            this.config = config;
        }

        @Override
        public void visit(Heading heading) {
            insertHardBreak();
            super.visit(heading);
            insertHardBreak();
        }

        @Override
        public void visit(Paragraph paragraph) {
            var parent = paragraph.getParent();
            boolean withoutBreak = parent instanceof ListBlock || parent instanceof ListItem;

            if (!withoutBreak) insertHardBreak();
            super.visit(paragraph);
            if (!withoutBreak) insertHardBreak();
        }

        @Override
        public void visit(ThematicBreak thematicBreak) {
            if (config.horizontalRuleCreateDocument) {
                buildAndFlush();
            }
            super.visit(thematicBreak);
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            currentContent.append(' ');
            super.visit(softLineBreak);
        }

        @Override
        public void visit(HardLineBreak hardLineBreak) {
            insertHardBreak();
            super.visit(hardLineBreak);
        }

        @Override
        public void visit(BulletList bulletList) {
            insertHardBreak();
            var child = bulletList.getFirstChild();
            while (child != null) {
                if (child instanceof ListItem listItem) {
                    currentContent.append(bulletList.getMarker()).append(' ');
                    super.visit(listItem);
                    insertSoftBreak();
                }
                child = child.getNext();
            }
            insertHardBreak();
        }

        @Override
        public void visit(OrderedList orderedList) {
            insertHardBreak();
            int number = orderedList.getMarkerStartNumber();
            var child = orderedList.getFirstChild();
            while (child != null) {
                if (child instanceof ListItem listItem) {
                    currentContent
                            .append(number)
                            .append(orderedList.getMarkerDelimiter())
                            .append(' ');
                    super.visit(listItem);
                    insertSoftBreak();
                }
                child = child.getNext();
                number++;
            }
            insertHardBreak();
        }

        @Override
        public void visit(ListItem listItem) {
            insertSoftBreak();
            super.visit(listItem);
            insertSoftBreak();
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            currentContent.append(strongEmphasis.getOpeningDelimiter());
            visitChildren(strongEmphasis);
            currentContent.append(strongEmphasis.getClosingDelimiter());
        }

        @Override
        public void visit(Emphasis emphasis) {
            currentContent.append(emphasis.getOpeningDelimiter());
            visitChildren(emphasis);
            currentContent.append(emphasis.getClosingDelimiter());
        }

        @Override
        public void visit(Code code) {
            currentContent.append('`').append(code.getLiteral()).append('`');
            super.visit(code);
        }

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            if (!config.includeCodeBlock) {
                buildAndFlush();
            }

            insertHardBreak();
            currentContent
                    .append(fencedCodeBlock.getFenceCharacter().repeat(fencedCodeBlock.getOpeningFenceLength()))
                    .append(fencedCodeBlock.getInfo())
                    .append('\n')
                    .append(fencedCodeBlock.getLiteral())
                    .append(fencedCodeBlock.getFenceCharacter().repeat(fencedCodeBlock.getClosingFenceLength()));
            super.visit(fencedCodeBlock);
            insertHardBreak();
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            if (!config.includeBlockquote) {
                buildAndFlush();
            }

            insertHardBreak();
            var child1 = blockQuote.getFirstChild();
            while (child1 != null) {
                if (child1 instanceof Paragraph paragraph) {
                    var child2 = paragraph.getFirstChild();
                    while (child2 != null) {
                        if (child2 instanceof Text text) {
                            currentContent.append("> ").append(text.getLiteral());
                            insertSoftBreak();
                        }
                        child2 = child2.getNext();
                    }
                }
                child1 = child1.getNext();
            }
            insertHardBreak();
        }

        @Override
        public void visit(Link link) {
            currentContent.append('[');
            var child = link.getFirstChild();
            while (child != null) {
                if (child instanceof Text text) {
                    currentContent.append(text.getLiteral());
                }
                child = child.getNext();
            }
            currentContent.append("](").append(link.getDestination()).append(')');
        }

        @Override
        public void visit(Text text) {
            if (currentContent.length() > config.maxChunkLength) {
                buildAndFlush();
            }

            if (text.getParent() instanceof Heading heading) {
                currentHeaderLevel = heading.getLevel();
                if (headerHierarchy.containsKey(currentHeaderLevel) || hasTextContent) {
                    buildAndFlush();
                }
                headerHierarchy.put(currentHeaderLevel, text.getLiteral());
                currentContent.append("#".repeat(currentHeaderLevel)).append(' ');
                currentContent.append(text.getLiteral());
            } else {
                hasTextContent = true;
                currentContent.append(text.getLiteral());
            }

            super.visit(text);
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

                hasTextContent = false;
                currentContent = new StringBuilder();
                rebuildHeaderHierarchy(currentContent, currentHeaderLevel);
            }
        }

        private void rebuildHeaderHierarchy(StringBuilder builder, int untilLevel) {
            for (int i = 1; i < untilLevel; i++) {
                String headerText = headerHierarchy.get(i);
                if (headerText != null) {
                    builder.append("#".repeat(i)).append(' ').append(headerText).append("\n\n");
                }
            }
        }

        private void insertSoftBreak() {
            if (!currentContent.isEmpty() && currentContent.charAt(currentContent.length() - 1) != '\n') {
                currentContent.append('\n');
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
