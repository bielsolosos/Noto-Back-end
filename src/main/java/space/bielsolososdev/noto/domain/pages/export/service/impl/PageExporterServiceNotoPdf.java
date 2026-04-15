package space.bielsolososdev.noto.domain.pages.export.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import space.bielsolososdev.noto.core.enums.MimeTypeEnum;
import space.bielsolososdev.noto.domain.pages.export.enums.TemplatePathEnum;
import space.bielsolososdev.noto.domain.pages.export.utils.TemplateLoaderUtils;
import space.bielsolososdev.noto.domain.pages.export.service.PageExporterService;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.utils.NotoAttributeProvider;
import space.bielsolososdev.noto.domain.pages.utils.NotoNodeRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PageExporterServiceNotoPdf implements PageExporterService {

    @Override
    public byte[] getBytesFromPage(Page entity) {
        String markdownFormatted = String.format("# %s\n\n%s", entity.getTitle(), entity.getContent());

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(TablesExtension.create()));
        options.set(TablesExtension.COLUMN_SPANS, true);
        options.set(TablesExtension.APPEND_MISSING_COLUMNS, true);
        options.set(TablesExtension.DISCARD_EXTRA_COLUMNS, true);

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).attributeProviderFactory(new NotoAttributeProvider.Factory()).nodeRendererFactory(new NotoNodeRenderer.Factory()).build();

        Document document = parser.parse(markdownFormatted);
        String flexmarkHtml = renderer.render(document);

        String pdfTemplate = TemplateLoaderUtils.loadTemplate(TemplatePathEnum.NOTO_SIMPLE_PDF);
        String finalHtml = pdfTemplate.replace("{{CONTENT}}", flexmarkHtml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode(); // Habilita o motor rápido do OpenHTMLtoPDF

            // O segundo parâmetro é o Base URI. Como suas imagens do bucket S3 tem URL absoluta (https://...), pode ficar vazio.
            builder.withHtmlContent(finalHtml, "");

            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro catastrófico ao forjar o PDF do Noto", e);
        }
    }

    @Override
    public String getFileName(Page entity) {
        String title = entity.getTitle();
        return String.format("Export %s.pdf", title);
    }

    @Override
    public MimeTypeEnum getMimeType() {
        return MimeTypeEnum.PDF;
    }

}

