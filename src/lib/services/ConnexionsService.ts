/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ConnectionRequest } from '../models/ConnectionRequest';
import type { ConnectionResponse } from '../models/ConnectionResponse';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ConnexionsService {
    /**
     * Mes connexions acceptées
     * Retourne toutes les connexions de statut ACCEPTED de l'utilisateur connecté.
     * @returns ConnectionResponse Liste des connexions
     * @throws ApiError
     */
    public static getConnections(): CancelablePromise<Array<ConnectionResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/connections',
        });
    }
    /**
     * Envoyer une demande de connexion
     * Crée une connexion en statut PENDING. L'utilisateur cible peut l'accepter (ACCEPTED) ou la bloquer (BLOCKED).
     * @param requestBody
     * @returns ConnectionResponse Demande de connexion créée
     * @throws ApiError
     */
    public static createConnection(
        requestBody: ConnectionRequest,
    ): CancelablePromise<ConnectionResponse> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/connections',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                400: `Connexion déjà existante ou auto-connexion`,
            },
        });
    }
    /**
     * Demandes de connexion en attente
     * Retourne les demandes reçues (statut PENDING) en attente d'acceptation.
     * @returns ConnectionResponse Demandes en attente
     * @throws ApiError
     */
    public static getPendingRequests(): CancelablePromise<Array<ConnectionResponse>> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/connections/pending',
        });
    }
    /**
     * Supprimer une connexion
     * @param id
     * @returns void
     * @throws ApiError
     */
    public static deleteConnection(
        id: any,
    ): CancelablePromise<void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/connections/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * Accepter ou bloquer une connexion
     * Seul le destinataire peut modifier le statut. Valeurs acceptées : `ACCEPTED`, `BLOCKED`.
     * @param id ID de la connexion
     * @param value Nouveau statut
     * @returns ConnectionResponse Statut mis à jour
     * @throws ApiError
     */
    public static updateConnectionStatus(
        id: any,
        value: 'ACCEPTED' | 'BLOCKED',
    ): CancelablePromise<ConnectionResponse> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/connections/{id}/status',
            path: {
                'id': id,
            },
            query: {
                'value': value,
            },
            errors: {
                400: `Statut invalide ou non autorisé`,
            },
        });
    }
}
