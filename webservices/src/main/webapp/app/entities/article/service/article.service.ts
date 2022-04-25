import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IArticle, getArticleIdentifier } from '../article.model';

export type EntityResponseType = HttpResponse<IArticle>;
export type EntityArrayResponseType = HttpResponse<IArticle[]>;

@Injectable({ providedIn: 'root' })
export class ArticleService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/articles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(article: IArticle): Observable<EntityResponseType> {
    return this.http.post<IArticle>(this.resourceUrl, article, { observe: 'response' });
  }

  update(article: IArticle): Observable<EntityResponseType> {
    return this.http.put<IArticle>(`${this.resourceUrl}/${getArticleIdentifier(article) as number}`, article, { observe: 'response' });
  }

  partialUpdate(article: IArticle): Observable<EntityResponseType> {
    return this.http.patch<IArticle>(`${this.resourceUrl}/${getArticleIdentifier(article) as number}`, article, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IArticle>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IArticle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addArticleToCollectionIfMissing(articleCollection: IArticle[], ...articlesToCheck: (IArticle | null | undefined)[]): IArticle[] {
    const articles: IArticle[] = articlesToCheck.filter(isPresent);
    if (articles.length > 0) {
      const articleCollectionIdentifiers = articleCollection.map(articleItem => getArticleIdentifier(articleItem)!);
      const articlesToAdd = articles.filter(articleItem => {
        const articleIdentifier = getArticleIdentifier(articleItem);
        if (articleIdentifier == null || articleCollectionIdentifiers.includes(articleIdentifier)) {
          return false;
        }
        articleCollectionIdentifiers.push(articleIdentifier);
        return true;
      });
      return [...articlesToAdd, ...articleCollection];
    }
    return articleCollection;
  }
}
