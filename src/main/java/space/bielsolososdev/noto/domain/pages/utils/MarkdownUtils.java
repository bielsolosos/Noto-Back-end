package space.bielsolososdev.noto.domain.pages.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MarkdownUtils {

    /**
     * Intercepta tabelas que não têm a linha de separação |---| e injeta dinamicamente.
     */
    public String fixMissingTableSeparators(String rawMarkdown) {
        String[] lines = rawMarkdown.split("\n");
        StringBuilder fixed = new StringBuilder();
        boolean inTable = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            fixed.append(line).append("\n");

            String trimmed = line.trim();
            // Identifica se a linha parece uma tabela (Começa e termina com pipe)
            if (trimmed.startsWith("|") && trimmed.endsWith("|")) {

                // Se for a primeira linha da tabela, checamos a próxima linha
                if (!inTable) {
                    boolean nextLineIsSeparator = (i + 1 < lines.length) && lines[i + 1].trim().startsWith("|") && lines[i + 1].contains("-");

                    // Se não tem separador, a gente constrói um e injeta!
                    if (!nextLineIsSeparator) {
                        // Conta quantos pipes tem na linha para saber o número de colunas
                        long pipesCount = trimmed.chars().filter(ch -> ch == '|').count();
                        int colunas = (int) pipesCount - 1;

                        StringBuilder separator = new StringBuilder("|");
                        for (int c = 0; c < colunas; c++) {
                            separator.append("---|");
                        }
                        fixed.append(separator).append("\n");
                    }
                    inTable = true;
                }
            } else {
                inTable = false;
            }
        }

        return fixed.toString();
    }
}
