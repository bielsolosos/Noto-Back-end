package br.dev.bielsolosos.noto.domain.pages.export.factory;

import lombok.NoArgsConstructor;
import br.dev.bielsolosos.noto.core.enums.ExportTypeEnum;
import br.dev.bielsolosos.noto.domain.pages.export.service.PageExporterService;
import br.dev.bielsolosos.noto.domain.pages.export.service.impl.PageExporterServiceMd;
import br.dev.bielsolosos.noto.domain.pages.export.service.impl.PageExporterServiceNotoPdf;

@NoArgsConstructor
public class ExportPageFactory {

    /**
     * Método responsável por retornar a implementação do serviço de acordo com o tipo de arquivo a ser exportado.
     */
    public PageExporterService generatePageService(ExportTypeEnum exportType){
        switch (exportType){

            case MD -> {
                return new PageExporterServiceMd();
            }

            case NOTO_PDF -> {
                return new PageExporterServiceNotoPdf();
            }

            default ->  throw new IllegalArgumentException("Invalid export type");
        }
    }
}
