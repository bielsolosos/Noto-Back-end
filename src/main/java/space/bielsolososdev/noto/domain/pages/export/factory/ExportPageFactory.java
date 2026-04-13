package space.bielsolososdev.noto.domain.pages.export.factory;

import lombok.NoArgsConstructor;
import space.bielsolososdev.noto.core.enums.ExportTypeEnum;
import space.bielsolososdev.noto.domain.pages.export.service.PageExporterService;
import space.bielsolososdev.noto.domain.pages.export.service.impl.PageExporterServiceMd;

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

            default ->  throw new IllegalArgumentException("Invalid export type");
        }
    }
}
