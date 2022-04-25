import { IPerson } from 'app/entities/person/person.model';

export interface IArticle {
  id?: number;
  title?: string | null;
  content?: string | null;
  person?: IPerson;
}

export class Article implements IArticle {
  constructor(public id?: number, public title?: string | null, public content?: string | null, public person?: IPerson) {}
}

export function getArticleIdentifier(article: IArticle): number | undefined {
  return article.id;
}
