package space.bielsolososdev.noto.domain.pages.utils;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;

import java.util.HashSet;
import java.util.Set;

public class NotoNodeRenderer implements NodeRenderer {

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        Set<NodeRenderingHandler<?>> set = new HashSet<>();
        // Interceptamos a Tabela e o Bloco de Código
        set.add(new NodeRenderingHandler<>(TableBlock.class, this::renderTable));
        set.add(new NodeRenderingHandler<>(FencedCodeBlock.class, this::renderCodeBlock));
        return set;
    }

    private void renderTable(TableBlock node, NodeRendererContext context, HtmlWriter html) {
        // Exatamente como no seu TS: <table class="markdown-table">
        html.withAttr().attr("class", "markdown-table").tag("table");
        context.renderChildren(node);
        html.tag("/table");
    }

    private void renderCodeBlock(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
        String language = node.getInfo().toString();
        
        // <pre class="markdown-code-block"><code class="language-...">
        html.line();
        html.withAttr().attr("class", "markdown-code-block").tag("pre");
        
        if (!language.isEmpty()) {
            html.attr("class", "language-" + language);
        }
        html.tag("code");
        html.text(node.getContentChars().normalizeEOL());
        html.tag("/code");
        html.tag("/pre");
        html.line();
    }

    public static class Factory implements com.vladsch.flexmark.html.renderer.NodeRendererFactory {
        @Override
        public NodeRenderer apply(DataHolder options) {
            return new NotoNodeRenderer();
        }
    }
}