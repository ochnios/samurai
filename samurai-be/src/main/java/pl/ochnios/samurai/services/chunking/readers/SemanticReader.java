package pl.ochnios.samurai.services.chunking.readers;

import static org.apache.tika.parser.pdf.PDFParserConfig.OCR_STRATEGY.NO_OCR;

import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParserConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import pl.ochnios.samurai.services.chunking.converters.Html2MdConverter;
import pl.ochnios.samurai.services.chunking.exception.ChunkingException;
import pl.ochnios.samurai.services.chunking.readers.markdown.MarkdownReader;

@Slf4j
public class SemanticReader implements DocumentReader {
    private final Resource resource;
    private final Html2MdConverter html2MdConverter;

    public SemanticReader(Resource resource) {
        this.resource = resource;
        this.html2MdConverter = new Html2MdConverter();
    }

    @Override
    public List<Document> get() {
        try (InputStream stream = resource.getInputStream()) {
            var filename = resource.getFilename();
            var parser = new AutoDetectParser();
            var handler = new ExpandedTitleContentHandler(new ToHTMLContentHandler());

            var metadata = new Metadata();
            var context = new ParseContext();

            context.set(PDFParserConfig.class, getPdfParserConfig());
            context.set(OfficeParserConfig.class, getOfficeParserConfig());

            parser.parse(stream, handler, metadata, context);
            var htmlContent = handler.toString();
            log.debug("HTML extracted from '{}': ===\n{}\n===", filename, htmlContent);

            var mdContent = html2MdConverter.convert(htmlContent);
            log.debug("Markdown extracted from '{}': ===\n{}\n===", filename, mdContent);

            var mdResource = new ByteArrayResource(mdContent.getBytes(), filename);
            var mdReader = new MarkdownReader(mdResource);
            var chunks = mdReader.get();
            log.debug("Chunks extracted from '{}': ===\n{}\n===", filename, chunks);

            return chunks;
        } catch (Exception ex) {
            throw new ChunkingException("Failed to extract document content from " + resource.getFilename(), ex);
        }
    }

    private PDFParserConfig getPdfParserConfig() {
        var config = new PDFParserConfig();
        config.setExtractMarkedContent(true);
        config.setExtractInlineImages(true);
        config.setExtractUniqueInlineImagesOnly(true);
        config.setOcrStrategy(NO_OCR);
        return config;
    }

    private OfficeParserConfig getOfficeParserConfig() {
        var config = new OfficeParserConfig();
        config.setIncludeHeadersAndFooters(false);
        return config;
    }
}
