import { JsonValue } from "@prisma/client/runtime/library";

export interface PageCreateDto {
  title: string;
  content: JsonValue;
}

export interface PageUpdateDTO {
  title?: string;
  content?: any;
  archived?: boolean;
}

export type Page = {
  id: string;
  title: string;
  content: JsonValue;
  createdAt: Date;
  updatedAt: Date;
  archived: boolean;
};
