/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { UserProfile } from '../models/UserProfile';
import type { UserRequest } from '../models/UserRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UtilisateursService {
    /**
     * Créer un utilisateur
     * Crée un nouvel utilisateur avec son contexte académique et ses intérêts déclarés. Accessible publiquement pour l'enregistrement initial.
     * @param requestBody
     * @returns UserProfile Utilisateur créé
     * @throws ApiError
     */
    public static createUser(
        requestBody: UserRequest,
    ): CancelablePromise<UserProfile> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Email déjà utilisé ou données invalides`,
            },
        });
    }
    /**
     * Obtenir un profil complet
     * Retourne le profil complet (User + contexte académique + intérêts + nb de connexions).
     * @param id ID de l'utilisateur
     * @returns UserProfile Profil trouvé
     * @throws ApiError
     */
    public static getProfile1(
        id: any,
    ): CancelablePromise<UserProfile> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}',
            path: {
                'id': id,
            },
            errors: {
                404: `Utilisateur introuvable`,
            },
        });
    }
    /**
     * Mettre à jour le profil
     * @param id
     * @param requestBody
     * @returns UserProfile Profil mis à jour
     * @throws ApiError
     */
    public static updateProfile1(
        id: any,
        requestBody: UserRequest,
    ): CancelablePromise<UserProfile> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/users/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                404: `Utilisateur introuvable`,
            },
        });
    }
}
