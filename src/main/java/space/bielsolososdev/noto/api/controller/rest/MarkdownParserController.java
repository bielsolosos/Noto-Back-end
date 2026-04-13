package space.bielsolososdev.noto.api.controller.rest;

import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.bielsolososdev.noto.domain.pages.service.PageService;
import space.bielsolososdev.noto.domain.pages.utils.NotoAttributeProvider;
import space.bielsolososdev.noto.domain.pages.utils.NotoNodeRenderer;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class MarkdownParserController {

    private final PageService pageService;

    @GetMapping
    public String returnMarkdownToHtml() {
        String markdown = pageService.getById(UUID.fromString("9c5e2e11-52ff-454d-a9cf-96dc7cd2c5eb")).getContent();

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        options.set(TablesExtension.COLUMN_SPANS, true);
        options.set(TablesExtension.APPEND_MISSING_COLUMNS, true);
        options.set(TablesExtension.DISCARD_EXTRA_COLUMNS, true);

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options)
                .attributeProviderFactory(new NotoAttributeProvider.Factory())
                .nodeRendererFactory(new NotoNodeRenderer.Factory())
                .build();

        Document document = parser.parse(markdown);
        return renderer.render(document);
    }
}
