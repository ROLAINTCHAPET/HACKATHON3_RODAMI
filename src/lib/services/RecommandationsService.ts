/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserProfile } from '../models/UserProfile';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RecommandationsService {
    /**
     * Flux PUSH — Découverte hors bulle de filtre
     * Retourne des utilisateurs qui ne partagent **aucun intérêt** avec le demandeur.
     *
     * **Objectif** : prévenir la bulle de filtre — exposer l'utilisateur à d'autres univers.
     *
     * **Algorithme** : exclusion des tags communs + sélection aléatoire (`ORDER BY RANDOM()`).
     *
     * @param id
     * @returns UserProfile Liste de profils hors bulle
     * @throws ApiError
     */
    public static getDiscovery(
        id: any,
    ): CancelablePromise<Array<UserProfile>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}/discovery',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Flux PULL — Recommandations par intérêts communs
     * Retourne les utilisateurs avec le plus d'intérêts communs.
     *
     * **Algorithme** : score = nombre de tags partagés → tri décroissant.
     *
     * **Cache** : résultat mis en cache Redis pendant 5 minutes.
     * Invalider via `POST /api/users/{id}/interests` ou `DELETE /api/users/{id}/interests/{tag}`.
     *
     * @param id
     * @returns UserProfile Liste de profils recommandés (triés par score)
     * @throws ApiError
     */
    public static getRecommendations(
        id: any,
    ): CancelablePromise<Array<UserProfile>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}/recommendations',
            path: {
                'id': id,
            },
        });
    }
}
