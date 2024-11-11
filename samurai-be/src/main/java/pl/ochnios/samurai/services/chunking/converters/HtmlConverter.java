package pl.ochnios.samurai.services.chunking.converters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class HtmlConverter {

    public String toMarkdown(String html) {
        var doc = Jsoup.parse(html);
        var markdown = new StringBuilder();

        for (var element : doc.body().children()) {
            processElement(element, markdown);
        }

        return markdown.toString().trim();
    }

    private void processElement(Element element, StringBuilder markdown) {
        var text = element.text().trim();
        switch (element.tagName().toLowerCase()) {
            case "b", "strong" -> markdown.append("**").append(text).append("**");
            case "i", "em" -> markdown.append("*").append(text).append("*");
            case "h1", "title" -> markdown.append("# ").append(text).append("\n\n");
            case "h2" -> markdown.append("## ").append(text).append("\n\n");
            case "h3" -> markdown.append("### ").append(text).append("\n\n");
            case "h4" -> markdown.append("#### ").append(text).append("\n\n");
            case "h5" -> markdown.append("##### ").append(text).append("\n\n");
            case "h6" -> markdown.append("###### ").append(text).append("\n\n");
            case "br" -> markdown.append(text).append("\n\n");
            case "a" -> {
                var href = element.attr("href");
                if (!href.isEmpty()) {
                    markdown.append("[").append(text).append("](").append(href).append(")");
                } else {
                    markdown.append(text);
                }
            }
            case "p", "div" -> {
                var children = element.children();
                if (!children.isEmpty()) {
                    for (Element child : children) {
                        processElement(child, markdown);
                    }
                } else {
                    markdown.append(text).append("\n\n");
                }
            }
            default -> {
                if (!text.trim().isEmpty()) {
                    markdown.append(text).append("\n");
                }
            }
        }
    }
}
