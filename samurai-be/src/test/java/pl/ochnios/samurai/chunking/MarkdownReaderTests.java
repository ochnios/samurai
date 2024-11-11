package pl.ochnios.samurai.chunking;

import org.junit.jupiter.api.Test;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReader;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownReaderTests {

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
        String markdown = """
# Header 1
Content 1
## Subheader 1
Content 2
# Header 2
Content 3""";
        var reader = new MarkdownReader(markdown);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(3);
        assertThat(documents.get(0).getContent()).contains("# Header 1\n\nContent 1");
        assertThat(documents.get(1).getContent()).contains("# Header 1\n\n## Subheader 1\n\nContent 2");
        assertThat(documents.get(2).getContent()).contains("# Header 2\n\nContent 3");
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
        var reader = new MarkdownReader(markdown);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(3);
        assertThat(documents.get(0).getContent()).contains("# Main Header\n\n## Sub Header 1\n\nContent 1");
        assertThat(documents.get(1).getContent())
                .contains("# Main Header\n\n## Sub Header 1\n\n### Sub-sub Header\n\nContent 2");
        assertThat(documents.get(2).getContent()).contains("# Main Header\n\n## Sub Header 2\n\nContent 3");
    }

    @Test
    void shouldHandleStrongEmphasis() {
        String markdown = "Here is sample **strong emphasis**";
        testEqualInputOutput(markdown);
    }

    @Test
    void shouldHandleEmphasis() {
        String markdown = "Here is sample *emphasis*";
        testEqualInputOutput(markdown);
    }

    @Test
    void shouldHandleLinks() {
        String markdown = "Here is sample [link](https://example.com)";
        testEqualInputOutput(markdown);
    }

    @Test
    void shouldHandleCode() {
        String markdown = "Here is sample code `print('Hello world')`";
        testEqualInputOutput(markdown);
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
        testEqualInputOutput(markdown);
    }

    @Test
    void shouldHandleBlockquotes() {
        String markdown =
                """
# Quote Example

Here's a quote:

> This is a blockquote
> With multiple lines

Regular text continues""";
        testEqualInputOutput(markdown);
    }

    private void testEqualInputOutput(String markdown) {
        // given
        var reader = new MarkdownReader(markdown);

        // when
        var documents = reader.get();

        // then
        assertThat(documents).hasSize(1);
        assertThat(documents.getFirst().getContent()).isEqualTo(markdown);
    }
}
