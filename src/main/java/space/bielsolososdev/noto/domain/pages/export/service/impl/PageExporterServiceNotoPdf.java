package space.bielsolososdev.noto.domain.pages.export.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import space.bielsolososdev.noto.core.enums.MimeTypeEnum;
import space.bielsolososdev.noto.domain.pages.export.service.PageExporterService;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.utils.NotoAttributeProvider;
import space.bielsolososdev.noto.domain.pages.utils.NotoNodeRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PageExporterServiceNotoPdf implements PageExporterService {

    private static final String PDF_TEMPLATE = """
                        <!DOCTYPE html>
                        <html lang="pt-BR">
                        <head>
                            <meta charset="UTF-8" />
                        <style>
                            /* A Mágica do A4 acontece aqui */
                            @page {
                                size: A4;
                                margin: 20mm;
            
                                /* Coloca o número da página no rodapé direito */
                                @bottom-right {
                                    content: counter(page);
                                    font-family: sans-serif;
                                    font-size: 10pt;
                                    color: #666;
                                }
                            }
            
                            /* Estilos base */
                            body {
                                font-family: 'Helvetica', 'Arial', sans-serif;
                                line-height: 1.6;
                                color: #1a1a1a;
                                margin: 0;
                                padding: 0;
                            }
            
                            /* Headers */
                            .markdown-h1 {
                                font-size: 28px;
                                font-weight: 800;
                                line-height: 1.2;
                                margin-top: 30px;
                                margin-bottom: 15px;
                                color: #111827;
                                border-bottom: 1px solid #e5e7eb;
                                padding-bottom: 10px;
                            }
            
                            .markdown-h2 {
                                font-size: 24px;
                                font-weight: 700;
                                margin-top: 25px;
                                margin-bottom: 12px;
                                color: #1f2937;
                            }
            
                            .markdown-h3 { font-size: 20px; font-weight: 600; color: #374151; }
                            .markdown-h4 { font-size: 18px; font-weight: 600; color: #4b5563; }
            
                            /* Listas */
                            .markdown-ul, .markdown-ol {
                                margin: 15px 0;
                                padding-left: 30px;
                            }
                            .markdown-li {
                                margin: 5px 0;
                            }
            
                            /* Task lists */
                            .markdown-task-container {
                                margin: 15px 0;
                            }
                            .markdown-task-item {
                                margin: 5px 0;
                            }
                            .markdown-task-text-completed {
                                color: #9ca3af;
                                text-decoration: line-through;
                            }
            
                            /* Tabelas */
                            .markdown-table {
                                width: 100%;
                                border-collapse: collapse;
                                margin: 20px 0;
                                border: 1px solid #d1d5db;
                            }
                            .markdown-table th, .markdown-table td {
                                border: 1px solid #d1d5db;
                                padding: 10px 15px;
                                font-size: 14px;
                                text-align: left;
                                vertical-align: top;
                            }
                            .markdown-table tr:nth-child(even) {
                                background-color: #f9fafb;
                            }
            
                            /* Código */
                            .markdown-code-inline {
                                background-color: #f3f4f6;
                                border: 1px solid #e5e7eb;
                                padding: 2px 6px;
                                border-radius: 4px;
                                font-family: 'Courier New', Courier, monospace;
                                font-size: 13px;
                                color: #2563eb;
                            }
            
                            .markdown-code-block {
                                background-color: #f3f4f6;
                                border: 1px solid #e5e7eb;
                                padding: 15px;
                                border-radius: 6px;
                                margin: 20px 0;
                                font-family: 'Courier New', Courier, monospace;
                                font-size: 13px;
                                white-space: pre-wrap; /* Ajuda a não estourar a linha */
                                page-break-inside: avoid; /* Evita que o bloco quebre no meio da página */
                            }
            
                            /* Blockquote */
                            .markdown-blockquote {
                                border-left: 4px solid #2563eb;
                                background-color: #eff6ff;
                                margin: 15px 0;
                                padding: 10px 15px;
                                font-style: italic;
                                color: #4b5563;
                            }
            
                            /* Outros */
                            .markdown-hr {
                                border: none;
                                border-top: 1px solid #e5e7eb;
                                margin: 30px 0;
                            }
                            .markdown-strong { font-weight: 700; color: #000; }
                            .markdown-em { font-style: italic; color: #4b5563; }
                            .markdown-del { text-decoration: line-through; color: #9ca3af; }
                            .markdown-link { color: #2563eb; text-decoration: underline; }
            
                            /* Imagens (Sem Flexbox, centralizado via block/text-align) */
                            .markdown-img-container {
                        display: block;
                                text-align: center;
                                margin: 25px auto;
                        
                                /* Essa regra avisa o PDF: "Não corte a imagem no meio entre duas páginas" */
                                page-break-inside: avoid;
                            }
                            .markdown-img {
                                /* Regra 1: Nunca ultrapassar as margens laterais */
                                        max-width: 100%;\s
            
                                        /* Regra 2: A MÁGICA! Limitar a altura máxima.\s
                                           Uma folha A4 tem cerca de 1000px úteis. Limitando a 450px,\s
                                           garantimos que a imagem nunca consuma mais que meia página,\s
                                           sobrando espaço para o texto colar nela! */
                                        max-height: 450px;\s
            
                                        /* Regra 3: Manter a proporção para não achatar a imagem */
                                        width: auto;
                                        height: auto;
            
                                        /* Frescurinhas visuais */
                                        border: 1px solid #d1d5db;
                                        border-radius: 4px;
                                        box-shadow: 0 1px 3px rgba(0,0,0,0.1); /* O PDF entende box-shadow simples! */
                            }
                        </style>
                        </head>
                        <body>
                            {{CONTENT}} 
                        </body>
                        </html>
            """;

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

        String finalHtml = PDF_TEMPLATE.replace("{{CONTENT}}", flexmarkHtml);

        System.out.println(finalHtml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode(); // Habilita o motor rápido do OpenHTMLtoPDF

            // O segundo parâmetro é o Base URI. Como suas imagens do bucket S3 tem URL absoluta (https://...), pode ficar vazio.
            builder.withHtmlContent(finalHtml, "");

            builder.toStream(os);
            builder.run();

            byte[] byteArray = os.toByteArray();
            return byteArray;
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
