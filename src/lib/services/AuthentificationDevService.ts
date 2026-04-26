/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AuthentificationDevService {
    /**
     * Générer un JWT de test
     * Endpoint de développement pour obtenir un token JWT valide sans passer par Firebase.
     * @param requestBody
     * @returns string Token généré
     * @throws ApiError
     */
    public static generateDevToken(
        requestBody: string,
    ): CancelablePromise<string> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/auth/dev-token',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
