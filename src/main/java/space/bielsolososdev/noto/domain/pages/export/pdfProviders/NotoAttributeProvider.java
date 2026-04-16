package space.bielsolososdev.noto.domain.pages.export.pdfProviders;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.html.MutableAttributes;

public class NotoAttributeProvider implements AttributeProvider {

    @Override
    public void setAttributes(Node node, AttributablePart part, MutableAttributes attributes) {
        
        // 1. Headers (h1 a h6)
        if (node instanceof Heading heading) {
            attributes.addValue("class", "markdown-h" + heading.getLevel());
        }
        
        // 2. Bold e Italic
        else if (node instanceof StrongEmphasis) {
            attributes.addValue("class", "markdown-strong");
        }
        else if (node instanceof Emphasis) {
            attributes.addValue("class", "markdown-em");
        }
        
        // 3. Blocos de Código (```)
        else if (node instanceof FencedCodeBlock) {
            attributes.addValue("class", "markdown-code-block");
        }
        
        // 4. Código Inline (`)
        else if (node instanceof Code) {
            attributes.addValue("class", "markdown-code-inline");
        }
        
        // 5. Blockquote (>)
        else if (node instanceof BlockQuote) {
            attributes.addValue("class", "markdown-blockquote");
        }

        else if (node instanceof TableRow) {
            attributes.addValue("class", "markdown-tr");
        } else if (node instanceof TableCell) {
            attributes.addValue("class", "markdown-td");
        }

        // 6. Links e Imagens
        else if (node instanceof Link) {
            attributes.addValue("class", "markdown-link");
            attributes.addValue("target", "_blank");
            attributes.addValue("rel", "noopener noreferrer");
        }
        else if (node instanceof Image) {
            attributes.addValue("class", "markdown-img");
        }
    }

    // Fábrica obrigatória para o Flexmark registrar o seu Provider
    public static class Factory extends IndependentAttributeProviderFactory {
        @Override
        public AttributeProvider apply(LinkResolverContext context) {
            return new NotoAttributeProvider();
        }
    }
}