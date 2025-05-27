export interface PageCreateDto {
  title: string;
  content?: string;
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

export type Page = {
  id: string;
  title: string;
  content: string;
  createdAt: Date;
  updatedAt: Date;
  archived: boolean;
};
