package pl.ochnios.samurai.chunking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReader;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReaderConfig;

public class MarkdownReaderTests {

    private final MarkdownReaderConfig customConfig = MarkdownReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(true)
            .withIncludeBlockquote(true)
            .withIncludeTable(true)
            .withMaxChunkLength(4000)
            .withMinChunkLength(350)
            .build();

    private final MarkdownReaderConfig defaultConfig = MarkdownReaderConfig.defaultConfig();

    @Test
    void shouldParseSimpleMarkdown() {
        // given
        String markdown = "# Header\nThis is a test content.";
        var reader = new MarkdownReader(markdown);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(1);
        assertThat(documents.getFirst().getContent()).isEqualTo("# Header\n\nThis is a test content.");
    }

    @Test
    void shouldSplitOnMultipleHeaders() {
        // given
        String markdown = "# Header 1\n\nContent 1\n\n## Subheader 1\n\nContent 2\n\n# Header 2\n\nContent 3";
        var reader = new MarkdownReader(markdown, defaultConfig);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(3);
        assertThat(documents.get(0).getContent()).isEqualTo("# Header 1\n\nContent 1");
        assertThat(documents.get(1).getContent()).isEqualTo("# Header 1\n\n## Subheader 1\n\nContent 2");
        assertThat(documents.get(2).getContent()).isEqualTo("# Header 2\n\nContent 3");
    }

    @Test
    void shouldNotSplitOnMultipleHeaders() {
        // given
        String markdown = "# Header 1\n\nContent 1\n\n## Subheader 1\n\nContent 2\n\n# Header 2\n\nContent 3";
        testEqualInputOutput(markdown, customConfig);
    }

    @Test
    void shouldPreserveHeaderHierarchy() {
        // given
        String markdown =
                """
# Main Header
## Sub Header 1
Content 1
### Sub-sub Header
Content 2
## Sub Header 2
Content 3""";
        var reader = new MarkdownReader(markdown, defaultConfig);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(3);
        assertThat(documents.get(0).getContent()).isEqualTo("# Main Header\n\n## Sub Header 1\n\nContent 1");
        assertThat(documents.get(1).getContent())
                .isEqualTo("# Main Header\n\n## Sub Header 1\n\n### Sub-sub Header\n\nContent 2");
        assertThat(documents.get(2).getContent()).isEqualTo("# Main Header\n\n## Sub Header 2\n\nContent 3");
    }

    @Test
    void shouldHandleThematicBreak() {
        String markdown = "Here is sample text.\n\n---\n\nHere is another text after thematic break.";
        var reader = new MarkdownReader(markdown, customConfig);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(2);
        assertThat(documents.get(0).getContent()).isEqualTo("Here is sample text.");
        assertThat(documents.get(1).getContent()).isEqualTo("Here is another text after thematic break.");
    }

    @Test
    void shouldHandleParagraph() {
        String markdown = "Here is sample\nparagraph.\n\nHere is another paragraph after hard break.";
        var reader = new MarkdownReader(markdown, customConfig);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(1);
        assertThat(documents.getFirst().getContent())
                .isEqualTo("Here is sample paragraph.\n\nHere is another paragraph after hard break.");
    }

    @Test
    void shouldHandleBulletList() {
        String markdown = "Here is sample list:\n\n- Point 1\n- Point 2\n- Point 3";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleOrderedList() {
        String markdown = "Here is sample list:\n\n1. Point 1\n2. Point 2\n3. Point 3";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleStrongEmphasis() {
        String markdown = "Here is sample **strong emphasis**";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleEmphasis() {
        String markdown = "Here is sample *emphasis*";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleCode() {
        String markdown = "Here is sample code `print('Hello world')`";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleCodeBlocks() {
        String markdown =
                """
# Code Example

Here's some code:

```java
public class Test {
    void method() {}
}
```""";
        testEqualInputOutput(markdown, customConfig);
    }

    @Test
    void shouldHandleBlockquotes() {
        String markdown =
                """
# Quote Example

Here's a quote:

> This is a blockquote
> With multiple lines
> And *some* **formatting**

Regular text continues""";
        testEqualInputOutput(markdown, customConfig);
    }

    @Test
    void shouldHandleLinks() {
        String markdown = "[Some link](https://example.com)";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleJustText() {
        String markdown = "Here is some test content";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleHtmlBLocks() {
        String markdown = "<div>\n<strong>Here is some HTML content<strong>\n</div>";
        testEqualInputOutput(markdown, defaultConfig);
    }

    @Test
    void shouldHandleTables() {
        String markdown = """
|Header 1|Header 2|
|---|---|
|Value 1|Value 2|""";
        testEqualInputOutput(markdown, defaultConfig);
    }

    private void testEqualInputOutput(String markdown, MarkdownReaderConfig config) {
        // given
        var reader = new MarkdownReader(markdown, config);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(1);
        assertThat(documents.getFirst().getContent()).isEqualTo(markdown);
    }
}
