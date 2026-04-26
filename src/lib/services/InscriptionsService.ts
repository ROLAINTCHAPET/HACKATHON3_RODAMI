/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RegistrationResponse } from '../models/RegistrationResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class InscriptionsService {
    /**
     * Co-participants d'un événement (pont Matching)
     * Retourne les IDs des autres utilisateurs inscrits au même événement.
     *
     * **Usage** : permet au module Matching de suggérer des connexions entre participants
     * d'un même événement (via `sourceEventId` dans `ConnectionRequest`).
     *
     * @param id
     * @returns number Liste des IDs co-participants
     * @throws ApiError
     */
    public static getCoParticipants(
        id: any,
    ): CancelablePromise<Array<number>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/events/{id}/co-participants',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Liste des participants à un événement
     * @param id
     * @returns RegistrationResponse Liste des inscrits
     * @throws ApiError
     */
    public static getParticipants(
        id: any,
    ): CancelablePromise<Array<RegistrationResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/events/{id}/participants',
            path: {
                'id': id,
            },
        });
    }
    /**
     * S'inscrire à un événement
     * Inscrit l'utilisateur connecté à l'événement.
     *
     * **Validations :**
     * - L'événement doit être en statut PUBLISHED
     * - L'utilisateur ne doit pas déjà être inscrit
     * - La capacité max ne doit pas être atteinte (si définie)
     *
     * @param id ID de l'événement
     * @returns RegistrationResponse Inscription créée
     * @throws ApiError
     */
    public static register1(
        id: any,
    ): CancelablePromise<RegistrationResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/events/{id}/register',
            path: {
                'id': id,
            },
            errors: {
                400: `Déjà inscrit, événement complet ou non publié`,
            },
        });
    }
    /**
     * Se désinscrire d'un événement
     * @param id
     * @returns void
     * @throws ApiError
     */
    public static unregister(
        id: any,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/events/{id}/register',
            path: {
                'id': id,
            },
            errors: {
                400: `Utilisateur non inscrit`,
            },
        });
    }
    /**
     * Événements d'un utilisateur
     * Retourne tous les événements auxquels un utilisateur est inscrit.
     * @param id ID de l'utilisateur
     * @returns RegistrationResponse Liste des inscriptions
     * @throws ApiError
     */
    public static getUserRegistrations(
        id: any,
    ): CancelablePromise<Array<RegistrationResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}/registrations',
            path: {
                'id': id,
            },
        });
    }
}
