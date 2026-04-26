/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Interest } from '../models/Interest';
import type { InterestRequest } from '../models/InterestRequest';
import type { ServerResponse } from '../models/ServerResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class IntRTsService {
    /**
     * Lister les intérêts d'un utilisateur
     * @param id
     * @returns Interest Liste des intérêts
     * @throws ApiError
     */
    public static getInterests(
        id: any,
    ): CancelablePromise<Array<Interest>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}/interests',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Ajouter un intérêt
     * Ajoute un intérêt et invalide le cache Redis de recommandations PULL.
     * @param id
     * @param requestBody
     * @returns ServerResponse Intérêt ajouté
     * @throws ApiError
     */
    public static addInterest(
        id: any,
        requestBody: InterestRequest,
    ): CancelablePromise<ServerResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/{id}/interests',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * Supprimer un intérêt
     * @param id
     * @param tag Tag à supprimer (ex: football)
     * @returns void
     * @throws ApiError
     */
    public static removeInterest1(
        id: any,
        tag: any,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/users/{id}/interests/{tag}',
            path: {
                'id': id,
                'tag': tag,
            },
        });
    }
}
