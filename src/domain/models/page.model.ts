export interface PageCreateDto {
  title: string;
  content?: string;
  userId: string; //TODO: Você não deve passar o Id no input. deve pegar do token assim que implementar.
}

export interface PageUpdateDTO {
  title?: string;
  content?: any;
  archived?: boolean;
}

export interface PageSummaryDto {
  id: string;
  title: string;
  updatedAt: Date;
}
