package br.dev.bielsolosos.noto.domain.pages.export.utils;

import lombok.experimental.UtilityClass;
import br.dev.bielsolosos.noto.domain.pages.export.enums.TemplatePathEnum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class TemplateLoaderUtils {

    public String loadTemplate(TemplatePathEnum path) {
        try (InputStream is = TemplateLoaderUtils.class.getResourceAsStream(path.getTemplatePath())) {
            if (is == null) {
                throw new IllegalStateException("Template não encontrado no classpath: " + path.getTemplatePath());
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar o template do PDF", e);
        }
    }

}
