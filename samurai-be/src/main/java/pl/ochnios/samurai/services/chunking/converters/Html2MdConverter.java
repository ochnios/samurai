package pl.ochnios.samurai.services.chunking.converters;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.List;

public class Html2MdConverter {

    private final FlexmarkHtmlConverter converter;

    public Html2MdConverter() {
        this(new MutableDataSet()
                .set(FlexmarkHtmlConverter.SKIP_ATTRIBUTES, true)
                .set(FlexmarkHtmlConverter.SKIP_CHAR_ESCAPE, true)
                .set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(Parser.EXTENSIONS, List.of(TablesExtension.create())));
    }

    public Html2MdConverter(DataHolder options) {
        converter = FlexmarkHtmlConverter.builder(options).build();
    }

    public String convert(String html) {
        return converter.convert(html);
    }
}
